% Simple comparison of recommendations based on user ratings

clear
cla
clc

load u.data

n_users = 943;
n_movies = 1682;
n_ratings = 100000;
ini_val = NaN;

a = ini_val*ones(n_movies, n_users);   % Initialise ratings matrix
% Create ratings matrix
for i = 1:n_ratings
   u_index = u(i,1);
   m_index = u(i,2);
   rating = u(i,3);
   a(m_index, u_index) = rating;
end

avg_rating = nanmean(a,2);
%rating_var = nanvar(a,ones(1,n_users),2);

% How much alike have the users rated the movies?
user_likeness = -1*ones(n_users,n_users);
for i = 1:n_users
    for j = 1:i
        b = (a(:,i) - a(:,j)).^2;
        user_likeness(i,j) = nanmean(b);
        user_likeness(j,i) = user_likeness(i,j);
    end
end


% Simple recommender system
n_training = 850;
n_testing = n_users - n_training;
est_rat = NaN*ones(n_movies,n_testing);

% Find the k nearest neighbours
kNN = 2;
nearest_neigh = NaN*ones(kNN,n_testing);
for i = n_training + 1:n_training + n_testing
    % Finds the first k nearest neighbours greater than min, want to find
    % the k smallest
    [m mi] = sort(user_likeness(1:n_training,i)); 
    nearest_neigh(:,i - n_training) = mi(1:kNN);
    for j = 1:n_movies
        est_rat(j, i - n_training) = nanmean(a(j,nearest_neigh(:, i - n_training)));
    end
end

% Error before giving all movies an estimated rating, zero can mean either
% perfect OR simply no rating
error1 = nanmean((a(:,n_training+1:n_users) - est_rat).^2);

% Set all NaN to 2.5
ind = find(~(est_rat >= 0));
est_rat(ind) = 2.5;
error2 = nanmean((a(:,n_training+1:n_users) - est_rat).^2);
mean_tot_error = mean(error2)


% Idea for some clustering:
% Plot with x axis difference with avg_rating, y axis difference with
% largest variance item

% Plot similarity of one user compared to all the others
% figure(1)
% plot(user_likeness(4,:))