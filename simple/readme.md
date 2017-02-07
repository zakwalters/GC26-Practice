# Percpetron Net #

* Simple single layer percpetron network of two neurons that learn to identify patterns with more 1s or more 0s.

* The input is a sequence of 5 bits. The getDataSet() function returns an array of 100 pattern arrays. Each of the pattern arrays contains 6 elements - the first 5  are the inputs and the 6th is the desired output.

* It seems to learn best with a training rate of 1 - but is interesting to look at when the training rate is much smaller as the average error for each epoch can be seen to move up as well as down.

* The training terminates when the error is 0 - in this simple case 0 error is always achievable.
