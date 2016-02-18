include "globals.mzn";

var int: endTime;

% specify nr of adds/mults
int: adders = 2;
int: multers = 4;

int: nodes = 28;

%specify tree
array[1..30] of int: from = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12, 13, 13, 14, 15, 16, 17, 18, 18, 19, 19, 20, 21, 22, 23, 24, 25];
array[1..30] of int: to = [8, 8, 9, 9, 10, 10, 11, 11, 26, 12, 13, 27, 14, 16, 15, 17, 18, 18, 19, 19, 22, 20, 23, 21, 24, 24, 25, 25, 26, 27];
array[1..nodes] of int: opTimes = [2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1];

% specify clock cycles for operations
int: addTime = 1;
int: multTime = 2;

set of int: totOps = 1..nodes;

% X-led
array[totOps] of var 0..100: ops;

% Y-led
array[totOps] of var 1..adders+multers: diffTot;
constraint forall(i in 1..nodes)(if opTimes[i] == 1 then diffTot[i] <= adders else diffTot[i] > adders endif);

% DX
array[totOps] of var 1..2: totTimes;
constraint forall(i in 1..nodes)(if opTimes[i] == 1 then totTimes[i] < 2 else totTimes[i] > 1 endif);

% DY

array[totOps] of var 1..1: totCost;

%diffn for adders
%constraint diffn(adds, diffAdds, addTimes, addCost);
constraint diffn(ops, diffTot, totTimes, totCost);

constraint forall(i in index_set(from))((ops[from[i]+1] + opTimes[from[i]+1] <= ops[to[i]+1]));

constraint (ops[27] + addTime <= endTime);
constraint (ops[28] + addTime <= endTime);



solve :: seq_search([
      int_search(ops, first_fail, indomain_min, complete)])
    minimize endTime;

output ["Clock cycles: ", show(endTime), show(diffTot), show(ops)];

% mzn2fzn -G jacop AutoReg.mzn
% fzn-jacop -s -a AutoReg.fzn
