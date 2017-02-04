import random
from time import sleep
from copy import deepcopy

"""
A simple, 100 node Hopfield net to store and retrieve
representations of the numbers 0, 1 and 2.
"""

"""
Input patterns and stored patterns must have these
dimensions.
"""
no_of_rows = 10
no_of_columns = 10
no_of_nodes = no_of_rows * no_of_columns

# True = use input pattern, False = use random pattern
use_input_pattern = True

"""
Edit this to set a specific starting state for the net. The
corresponding line in the main loop must also be un-commented.
"""
input_pattern = (
    "----OO----"
    "---OOO----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "---OOOOO--"
    )

"""
Edit this to change the stored patterns. Note: thresholds
for nodes are all zero, so the inverse of any pattern
here will also be stored.
"""
patterns_to_store = [
    (
    "----OO----"
    "--OO--OO--"
    "-O------O-"
    "O--------O"
    "O--------O"
    "O--------O"
    "O--------O"
    "-O------O-"
    "--OO--OO--"
    "----OO----"
    ),
    (
    "----OO----"
    "---OOO----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "-----O----"
    "---OOOOO--"
    ),
    (
    "---OOOO---"
    "--O----O--"
    "-O------O-"
    "--------O-"
    "-------O--"
    "------O---"
    "-----O----"
    "---OO-----"
    "-OO-------"
    "OOOOOOOOOO"
    )
    ]


def process_pattern(pattern):

    """
    Takes a 100 character string, pattern, and
    converts it into a list of ints; -1 for a "-" (an "off"),
    and 1 for an "O" (an "on"). Returns this list of ints.
    """

    if len(pattern) != no_of_nodes:
        raise ValueError("Pattern must contain " + str(no_of_nodes) + " nodes.")

    encode = {"O":1, "-":-1}
    return [encode[c] for c in pattern]


def print_state(state, updating_node=None):

    """
    Takes the current state of the net, state, and prints it to
    the console, followed by a divider to indicate the end of an
    iteration. Returns null.
    """

    decode = ["X", "O", "-"]

    out = ""
    if updating_node != None:
        for y in range(0, no_of_nodes, no_of_rows):
            row = ""
            for x in range(0, no_of_columns):
                if x + y == updating_node:
                    row += decode[0]
                else:
                    row += decode[state[y + x]]
            out += row + "\n"

    else:
        for y in range(0, no_of_nodes, no_of_rows):
            row = ""
            for x in range(0, no_of_columns):
                row += decode[state[y + x]]
            out += row + "\n"

    print(out + "\n" + "~"*no_of_columns + "\n")


def get_initial_weights(stored_patterns, nodes):

    """
    Takes the list of processed patterns, stored_patterns, and the
    list of node values from the input pattern. Calculates initial
    weights to store the stored patterns, and stores these in a 2D
    array (first index is the start node of the connection, second
    index is the end node). Returns this 2D array.
    """

    weights = []

    for node_from in range(no_of_nodes):
        weights.append([])
        for node_to in range(no_of_nodes):
            if node_to == node_from:
                weights[node_from].append(0)
            else:
                total = 0
                for pattern in stored_patterns:
                    total += pattern[node_from] * pattern[node_to]
                weights[node_from].append(total)

    return weights


def update(nodes, weights):

    """
    Takes the current state, nodes, and updates each node in
    turn, in a random order. Returns the modified state.
    """

    update_order = [n for n in range(no_of_nodes)]
    random.shuffle(update_order)
    print_counter = 0

    for selected_node in update_order:
        total = 0
        for i in range(no_of_nodes):
            total += nodes[i] * weights[selected_node][i]

        if total > 0:
            nodes[selected_node] = 1
        elif total < 0:
            nodes[selected_node] = -1

        print_counter += 1
        if print_counter % 1 == 0: # Change frequncy of prints here
            print_state(nodes, updating_node=selected_node)
            sleep(0.1) # Just makes the output look nicer


def get_total_energy(pattern, weights):

    """
    Takes a pattern, pattern, and the weights of the net, weights,
    and returns the total Hopfield energy of the state.
    """

    total = 0
    for node_i in pattern:
        for node_j in pattern:
            total += weights[node_i][node_j] * node_i * node_j
    return total * (-0.5)


def get_random_input(density=""):

    """
    Returns a random starting state for the network. Setting density
    to "low" will give fewer active nodes, setting it to "high" will
    give more active nodes.
    """

    if density == "low":
        state = [random.choice([-1, -1, 1]) for n in range(no_of_nodes)]
    elif density == "high":
        state = [random.choice([-1, 1, 1]) for n in range(no_of_nodes)]
    else:
        state = [random.choice([-1, 1]) for n in range(no_of_nodes)]

    return state


def corrupt_pattern(pattern, level=0.05):

    """
    Corrupts an input pattern randomly to test whether it can be recovered.
    Takes the pattern to be corrupted, pattern, and a level between
    0 and 1 indicating the likelihood of each node being corrupted.
    """

    if level < 0 or level > 1:
        raise ValueError("Level of corruption must be between 0 and 1")

    for i in range(no_of_nodes):
        if random.random() < level:
            pattern[i] = random.choice([-1, 1])


def main():

    # Turn string patterns into int arrays
    stored_patterns = []
    for pattern in patterns_to_store:
        stored_patterns.append(process_pattern(pattern))

    if use_input_pattern:
        nodes = process_pattern(input_pattern)
        # Un-comment to add random noise to the input:
        corrupt_pattern(nodes, level=0.3)
    else:
        nodes = get_random_input()

    print("Starting state:\n")
    print_state(nodes)
    sleep(3)

    # Set the weights for all connections
    weights = get_initial_weights(stored_patterns, nodes)

    max_iterations = 100
    last_state = []

    current_iteration = 1

    # Main loop.
    while (current_iteration < max_iterations):
        print_state(nodes)
        last_state = deepcopy(nodes)
        update(nodes, weights)

        if nodes == last_state:
            print_state(nodes)
            print("Stable state reached.")
            break

        print("Iteration: " + str(current_iteration) + " complete.\n")
        sleep(1)

        current_iteration += 1

    else:
        print_state(nodes)
        print("Max iterations reached.")

    for i in range(len(stored_patterns)):
        inverse = [[0, -1, 1][x] for x in stored_patterns[i]]
        if nodes == stored_patterns[i]:
            print("Retrieved stored pattern " + str(i) + ".")
            break
        elif nodes == inverse:
            print("Retrieved inverse of pattern " + str(i) + ".")
            break
    else:
        print("Stable state was not in stored patterns.")

    print("Total energy: " + str(get_total_energy(nodes, weights)))

if __name__ == "__main__":
    main()
