include "globals.mzn";
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
%Output
output ["total cycles: ", show(endTime), "\nmultipliers: ", show(multiplications), "\nadders: ", show(additions)];
%First_fail = choose the variable with the smallest domain size
solve :: seq_search([int_search(additions ++ multiplications, first_fail, indomain_min, complete),int_search([endTime], first_fail, indomain_min,complete)])
    minimize endTime;


