% Analysis of the 100K dataset

clear
cla
clc

load u.data

n_users = 943;
n_movies = 1682;
n_ratings = 100000;
rat = [1 2 3 4 5];

ini_val = NaN;

data = ini_val*ones(n_movies, n_users);   % Initialise ratings matrix
% Create ratings matrix
for i = 1:n_ratings
   u_index = u(i,1);
   m_index = u(i,2);
   data(m_index, u_index) = u(i,3);
end

%%
% Histogram of ratings
hist = zeros(1,5);
for i = 1:5
    [hist(i) junk] = size(find(u(:,3)==i));
end

figure(1)
bar(rat, hist)
xlabel('Ratings')
ylabel('# of ratings')
title('100K ratings data set')

%%
% Normalisation of the data
% Each missing rating is replaced by 3
data3 = data;
data3(find(data3 ~= data3)) = 3; 

% Histogram of ratings
hist3 = zeros(1,5);
for i = 1:5
    [hist3(i) junk] = size(find(data3(:,:)==i));
end

figure(2)
bar(rat, hist3)
xlabel('Ratings')
ylabel('# of ratings')
title('100K ratings data set, non-rated items set to 3')

%%
% Normalisation of data using average rating of movie, rounded up to
% nearest integer
data_mav = data;
for i = 1:n_movies
    [junk nrat] = size(find(data_mav(i,:)==data_mav(i,:)));
    av = nansum(data_mav(i,:))/nrat;
    data_mav(i,find(data_mav(i,:) ~= data_mav(i,:))) = av;
end

data_mav = ceil(data_mav);

% Histogram of ratings
hist_mav = zeros(1,5);
for i = 1:5
    [hist_mav(i) junk] = size(find(data_mav(:,:)==i));
end

figure(3)
bar(rat, hist_mav)
xlabel('Ratings')
ylabel('# of ratings')
title('100K ratings data set, non-rated items set to average rating of item')

% Z-score, mean of user/movie-rating

%%
% min, max, median, mean, Z-score

%%
% Mean ratings of movies vs. number of movies

%%
% FPR vs. TPR for user-based CF, random items, popular items