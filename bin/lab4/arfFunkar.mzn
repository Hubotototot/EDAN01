include "globals.mzn";

var int: endTime;

% specify nr of adds/mults
int: adders = 2;
int: multers = 4;


% specify clock cycles for operations
int: addTime = 1;
int: multTime = 2;

set of int: totAdds = 1..12;
set of int: totMults = 1..16;

% X-led
array[totAdds] of var 0..100: adds;
array[totMults] of var 0..100: mults;

% Y-led
array[totAdds] of var 1..adders: diffAdds;
array[totMults] of var 1..multers: diffMults;

% DX
array[totAdds] of var 1..1: addTimes;
array[totMults] of var 2..2: multTimes;

% DY
array[totAdds] of var 1..1: addCost;
array[totMults] of var 1..1: multCost;

%diffn for adders
constraint diffn(adds, diffAdds, addTimes, addCost);


%diffn for multipliers
constraint diffn(mults, diffMults, multTimes, multCost);

% setup order of operations according to tree nodes
constraint (mults[1] + multTime <= adds[1]);
constraint (mults[2] + multTime <= adds[1]);

constraint (mults[3] + multTime <= adds[2]);
constraint (mults[4] + multTime <= adds[2]);

constraint (mults[5] + multTime <= adds[3]);
constraint (mults[6] + multTime <= adds[3]);

constraint (mults[7] + multTime <= adds[4]);
constraint (mults[8] + multTime <= adds[4]);

constraint (adds[2] + addTime <= adds[5]);

constraint (adds[3] + addTime <= adds[6]);

constraint (mults[9] + multTime <= adds[7]);
constraint (mults[10] + multTime <= adds[7]);

constraint (mults[11] + multTime <= adds[8]);
constraint (mults[12] + multTime <= adds[8]);

constraint (mults[13] + multTime <= adds[9]);
constraint (mults[14] + multTime <= adds[9]);

constraint (mults[15] + multTime <= adds[10]);
constraint (mults[16] + multTime <= adds[10]);

constraint (adds[1] + addTime <= adds[11]);
constraint (adds[9] + addTime <= adds[11]);

constraint (adds[4] + addTime <= adds[12]);
constraint (adds[10] + addTime <= adds[12]);

constraint (adds[5] + addTime <= mults[9]);

constraint (adds[6] + addTime <= mults[10]);

constraint (adds[5] + addTime <= mults[11]);

constraint (adds[6] + addTime <= mults[12]);

constraint (adds[7] + addTime <= mults[13]);

constraint (adds[8] + addTime <= mults[14]);

constraint (adds[7] + addTime <= mults[15]);

constraint (adds[8] + addTime <= mults[16]);

constraint (adds[11] + addTime <= endTime);
constraint (adds[12] + addTime <= endTime);



solve :: seq_search([
      int_search(adds ++ mults, first_fail, indomain_min, complete)])
    minimize endTime;

output ["Clock cycles: ", show(endTime), show(diffAdds), show(adds), show(diffMults), show(mults)];

% mzn2fzn -G jacop AutoReg.mzn
% fzn-jacop -s -a AutoReg.fzn
