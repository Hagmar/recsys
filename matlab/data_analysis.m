% Analysis of the 100K dataset

clear
cla
clc

% IF YOU CHANGE THE DATA SET, DON't FORGET TO CHANGE THE FILE NAMES
% FOR Z-SCORE AND GAUSSIAN NORMALISATION.

% 100K
load u.data

% 1m
% load ratings.dat
% size(ratings);
% u = ratings;

% 20m Too big
%u = csvread('ratings.csv',1,0);  

n_users = max(u(:,1));
n_movies = max(u(:,2));
[n_ratings junk] = size(u);
rat = [1 2 3 4 5];

ini_val = NaN;

data = ini_val*ones(n_movies, n_users);   % Initialise ratings matrix
% Create ratings matrix
for i = 1:n_ratings
   u_index = u(i,1);
   m_index = u(i,2);
   data(m_index, u_index) = u(i,3);
end

% Remove all non-rated items (specifically for 1m data set)
% not_rated = find(nansum(data')==0);   % Find all items which have not been rated
% [r c] = size(not_rated);
% index = find(nansum(data')~=0);
% data = data(index,:);
% n_movies = n_movies - c;


% Min, max, mean and median rating - not very interesting?
min_rat = nanmin(u(:,3))
max_rat = nanmax(u(:,3))
mean_rat = nanmean(u(:,3))
meadian_rat = nanmedian(u(:,3))

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

%% 3 for missing data
% Normalisation of the data
% Each missing rating is replaced by 3 (trivial with so many non-rated
% movies
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

%% Average by movies 
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
title('Non-rated items set to average rating of item')

%% Average rating by user
% Normalisation using average ratings of user, rounded up to nearest
% integer
data_uav = data;
for i = 1:n_users
    [nrat junk] = size(find(data_uav(:,i)==data_uav(:,i)));
    av = nansum(data_uav(:,i))/nrat;
    data_uav(find(data_uav(:,i) ~= data_uav(:,i)),i) = av;
end

data_uav = ceil(data_uav);

% Histogram of ratings
hist_uav = zeros(1,5);
for i = 1:5
    [hist_uav(i) junk] = size(find(data_uav(:,:)==i));
end

figure(4)
bar(rat, hist_uav)
xlabel('Ratings')
ylabel('# of ratings')
title('Non-rated items set to average rating by user')

%% Z-score
% Normalisation using z-score, based on items. All non-rated items have
% been set to the average rating of that item first
data_z = data_mav;
data_z = zscore(data_z);

% Remove items that weren't originally rated
index = find(isnan(data));
data_z(index) = NaN;

save_dz = zeros(size(u));
[r,c] = find(~isnan(data_z));
save_dz(:,1) = c;
save_dz(:,2) = r;
index = find(~isnan(data_z));
save_dz(:,3) = data_z(index);
csvwrite('z_score.csv',save_dz)

%% Gaussian Normalisation and Pearson Normalisation
gu_rating = nanmean(data);      % Average rating by each user
gu_rating = repmat(gu_rating, n_movies, 1);
diff = data - gu_rating;
enumerator = diff;      % Also Pearson normalisation
index = find(isnan(diff));
diff(index) = 0;  
denominator = sqrt(sum(diff.^2));
denominator = repmat(denominator, n_movies, 1);
g_norm = enumerator ./ denominator;

save_gn = zeros(size(u));
[r,c] = find(~isnan(g_norm));
save_gn(:,1) = c;
save_gn(:,2) = r;
index = find(~isnan(g_norm));
save_gn(:,3) = g_norm(index);
csvwrite('g_norm.csv',save_gn)


%% Save Pearson Normalisation
save_pn = zeros(size(u));
[r,c] = find(~isnan(enumerator));
save_pn(:,1) = c;
save_pn(:,2) = r;
index = find(~isnan(enumerator));
save_pn(:,3) = enumerator(index);
csvwrite('p_norm.csv', save_pn)