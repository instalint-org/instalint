def adder(n):
     num = 0
     while num < n:
         yield num
         num += 1
     return num