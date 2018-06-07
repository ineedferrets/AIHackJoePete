pfhit = [38.38, 27.66, 23.24, 19.6, 16.78, 15.33, 13.9, 12.43, 11.53, 10.53, 9.99, 9.82, 8.73, 7.75, 7.58, 6.94, 6.45, 6.1, 5.71, 4.82, 5.0, 4.86, 4.05, 4.4, 3.88, 3.7, 3.47, 3.59, 3.36, 2.78, 2.95, 2.93, 2.92, 2.47, 2.48, 2.25, 2.27, 2.35, 1.56, 2.08, 1.99, 2.08, 1.73, 1.71, 1.95, 1.29, 1.52, 1.52, 1.49, 1.36]
pnhit = [0.15, 0.25, 0.46, 0.53, 0.63, 0.8, 0.94, 0.97, 1.13, 0.95, 1.19, 1.31, 1.16, 1.32, 1.27, 1.34, 1.27, 1.31, 1.36, 1.19, 1.41, 1.12, 1.29, 1.41, 1.2, 1.35, 1.01, 1.36, 1.15, 1.21, 1.3, 1.11, 1.01, 1.14, 1.0, 0.93, 1.12, 0.98, 0.9, 0.9, 1.08, 0.95, 0.84, 0.89, 1.11, 0.89, 0.83, 0.89, 0.82, 0.79]
pmax = plotyy(x,pfhit,x,pnhit);
ylabel(pmax(2),"Success rate: final solution (%)");
ylabel(pmax(1),"Success rate: first hit (%)");
title("Success rate versus sampling rate: Noisy PMax");
xlabel("Resampling rate");
