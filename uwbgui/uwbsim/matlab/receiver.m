clc
close all
clear all

u2 = instrfindall

while (size(u2,1) > 0)
    delete(u2)
    clear u2
    break;
end

u2=udp('128.31.34.6', 9091, 'LocalPort', 9090 );
u2.Timeout = 20;

fopen(u2)% on 128.31.7.23

A = fread(u2, 8000)
rev = 1

fid = fopen('C:/uwblocs.txt','w');
fprintf(fid,'UWB locations revision %-1.2d\n',rev);
fprintf(fid,'%-0.2d,%-0.2d,%-0.2d\n',A);
fclose(fid);

fclose(u2);