a = [1 2 3];
b = [4 5 6];
c = 7.94682e6;
file_1 = fopen('C:/test.txt','w');
file_2 = fopen('C:/test2.txt','w');

fprintf(file_1,'%-5.2d %-5.2d %-5.2d \n',A)
fprintf(file_2,'b = [%13.6G %13.6G %13.6G] (km^3/s^2)\n\n c = %5.2f ',b,c)
fclose(file_1)
fclose(file_2)
