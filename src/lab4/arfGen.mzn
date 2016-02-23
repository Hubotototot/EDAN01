include "globals.mzn";
% sluttid
var int: endTime;

% Antal adders och multers samt antal depencendices och noder
int: adders = 2;
int: multers = 4;
int: dependencies = 30;
int: nodes = 28;

% Alla beroenden för noderna och operationstiderna stoppas in i en vektor
array[1..dependencies] of int: from = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12, 13, 13, 14, 15, 16, 17, 18, 18, 19, 19, 20, 21, 22, 23, 24, 25];
array[1..dependencies] of int: to = [8, 8, 9, 9, 10, 10, 11, 11, 26, 12, 13, 27, 14, 16, 15, 17, 18, 18, 19, 19, 22, 20, 23, 21, 24, 24, 25, 25, 26, 27];
array[1..nodes] of int: opTimes = [multTime, multTime, multTime, multTime, multTime, multTime, multTime, multTime, addTime, addTime, addTime, addTime, addTime, addTime, multTime, multTime, multTime, multTime, addTime, addTime, multTime, multTime, multTime, multTime, addTime, addTime, addTime, addTime];

% Tiden det tar för addition och multiplikation
int: addTime = 1;
int: multTime = 2;

set of int: totOps = 1..nodes;

% X-led
array[totOps] of var 0..100: ops;

% Y-led
array[totOps] of var 1..adders+multers: diffTot;
constraint forall(i in 1..nodes)(if opTimes[i] == addTime then diffTot[i] <= adders else diffTot[i] > adders endif);

% DX
array[totOps] of var 1..2: totTimes;
constraint forall(i in 1..nodes)(if opTimes[i] == addTime then totTimes[i] < 2 else totTimes[i] > 1 endif);

% DY - alltid ett
array[totOps] of var 1..1: totCost;

%diffn constraint för adders och multers
constraint diffn(ops, diffTot, totTimes, totCost);

constraint forall(i in index_set(from))((ops[from[i]+1] + opTimes[from[i]+1] <= ops[to[i]+1]));
constraint (ops[nodes-1] + addTime <= endTime);
constraint (ops[nodes] + addTime <= endTime);

solve :: seq_search([
      int_search(ops, first_fail, indomain_min, complete)])
    minimize endTime;

output ["Clock cycles: ", show(endTime), show(diffTot), show(ops)];

% mzn2fzn -G jacop AutoReg.mzn
% fzn-jacop -s -a AutoReg.fzn
