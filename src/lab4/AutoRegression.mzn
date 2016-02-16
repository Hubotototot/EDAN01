include "globals.mzn";
include "diffn.mzn";
%Sluttiden som ska optimeras
var int: endTime;
%Antal multipliers och adders
int: nbrMultipliers;
int: nbrAdders;
%Antal klockcyklar för multipliers respektive adders
int: multiplierClock;
int: adderClock;
%Antal multiplikationer och additioner
set of int: totalMultiplications;
set of int: totalAdditions;
%Specificika multipliers eller adders
array[totalMultiplications] of var 1..nbrMultipliers: pMul;
array[totalAdditions] of var 1..nbrAdders: pAdd;
%Klockcykel som multiplikation och addition startar på
array[totalMultiplications] of var 0..100: multiplications;
array[totalAdditions] of var 0..100: additions;
%Klockcykelkostnad för multipliers och adders som läses in i början och sen behövs för cumulative (bygga rektangel)
array[totalMultiplications] of int: multiplierClocks = [multiplierClock, multiplierClock, multiplierClock, 
                                    multiplierClock, multiplierClock, multiplierClock,
                                    multiplierClock, multiplierClock, multiplierClock, 
                                    multiplierClock, multiplierClock, multiplierClock,
                                    multiplierClock, multiplierClock, multiplierClock, 
                                    multiplierClock];

array[totalAdditions] of int: adderClocks = [adderClock, adderClock, adderClock, 
                              adderClock, adderClock, adderClock,
                              adderClock, adderClock, adderClock, 
                              adderClock, adderClock, adderClock];
%Resurskostnad för multipliera och adders är 1, (höjden i y-axel)
array[totalMultiplications] of int: mulCost = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1];
array[totalAdditions] of int: addCost = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1];
%Cumulative: bygga upp rektanglar
constraint cumulative(multiplications, multiplierClocks, mulCost, nbrMultipliers);
constraint cumulative(additions, adderClocks, addCost, nbrAdders);
constraint diff2([[multiplications[1], pMul[1], multiplierClocks[1], mulCost[1]],
[multiplications[2], pMul[2], multiplierClocks[2], mulCost[2]],
[multiplications[3], pMul[3], multiplierClocks[3], mulCost[3]],
[multiplications[4], pMul[4], multiplierClocks[4], mulCost[4]],
[multiplications[5], pMul[5], multiplierClocks[5], mulCost[5]],
[multiplications[6], pMul[6], multiplierClocks[6], mulCost[6]],
[multiplications[7], pMul[7], multiplierClocks[7], mulCost[7]],
[multiplications[8], pMul[8], multiplierClocks[8], mulCost[8]],
[multiplications[9], pMul[9], multiplierClocks[9], mulCost[9]],
[multiplications[10], pMul[10], multiplierClocks[10], mulCost[10]],
[multiplications[11], pMul[11], multiplierClocks[11], mulCost[11]],
[multiplications[12], pMul[12], multiplierClocks[12], mulCost[12]],
[multiplications[13], pMul[13], multiplierClocks[13], mulCost[13]],
[multiplications[14], pMul[14], multiplierClocks[14], mulCost[14]],
[multiplications[15], pMul[15], multiplierClocks[15], mulCost[15]],
[multiplications[16], pMul[16], multiplierClocks[16], mulCost[16]]]);
constraint diff2([[additions[1], pAdd[1],adderClocks[1], addCost[1]],
[additions[2], pAdd[2],adderClocks[2], addCost[2]],
[additions[3], pAdd[3],adderClocks[3], addCost[3]],
[additions[4], pAdd[4],adderClocks[4], addCost[4]],
[additions[5], pAdd[5],adderClocks[5], addCost[5]],
[additions[6], pAdd[6],adderClocks[6], addCost[6]],
[additions[7], pAdd[7],adderClocks[7], addCost[7]],
[additions[8], pAdd[8],adderClocks[8], addCost[8]],
[additions[9], pAdd[9],adderClocks[9], addCost[9]],
[additions[10], pAdd[10],adderClocks[10], addCost[10]],
[additions[11], pAdd[11],adderClocks[11], addCost[11]],
[additions[12], pAdd[12],adderClocks[12], addCost[12]]]);
%Constraints utifrån trädet i labbhandledningen
constraint (multiplications[1] + multiplierClock <= additions[1]);
constraint (multiplications[2] + multiplierClock <= additions[1]);
constraint (multiplications[3] + multiplierClock <= additions[2]);
constraint (multiplications[4] + multiplierClock <= additions[2]);
constraint (multiplications[5] + multiplierClock <= additions[3]);
constraint (multiplications[6] + multiplierClock <= additions[3]);
constraint (multiplications[7] + multiplierClock <= additions[4]);
constraint (multiplications[8] + multiplierClock <= additions[4]);
constraint (additions[2] + adderClock <= additions[5]);
constraint (additions[3] + adderClock <= additions[6]);
constraint (multiplications[9] + multiplierClock <= additions[7]);
constraint (multiplications[10] + multiplierClock <= additions[7]);
constraint (multiplications[11] + multiplierClock <= additions[8]);
constraint (multiplications[12] + multiplierClock <= additions[8]);
constraint (multiplications[13] + multiplierClock <= additions[9]);
constraint (multiplications[14] + multiplierClock <= additions[9]);
constraint (multiplications[15] + multiplierClock <= additions[10]);
constraint (multiplications[16] + multiplierClock <= additions[10]);
constraint (additions[1] + adderClock <= additions[11]);
constraint (additions[9] + adderClock <= additions[11]);
constraint (additions[4] + adderClock <= additions[12]);
constraint (additions[10] + adderClock <= additions[12]);
constraint (additions[5] + adderClock <= multiplications[9]);
constraint (additions[6] + adderClock <= multiplications[10]);
constraint (additions[5] + adderClock <= multiplications[11]);
constraint (additions[6] + adderClock <= multiplications[12]);
constraint (additions[7] + adderClock <= multiplications[13]);
constraint (additions[8] + adderClock <= multiplications[14]);
constraint (additions[7] + adderClock <= multiplications[15]);
constraint (additions[8] + adderClock <= multiplications[16]);
constraint (additions[11] + adderClock <= endTime);
constraint (additions[12] + adderClock <= endTime);
%diff2


%Output
output ["total cycles: ", show(endTime), "\nmultipliers: ", show(multiplications), "\nadders: ", show(additions)];
%First_fail = choose the variable with the smallest domain size
solve :: seq_search([int_search(additions ++ multiplications, first_fail, indomain_min, complete),int_search([endTime], first_fail, indomain_min,complete)])
    minimize endTime;


