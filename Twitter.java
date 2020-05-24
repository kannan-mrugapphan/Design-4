// 355.
public class Tweet {
    //each tweet object is a row in tweets table with fields id(tweet id), createdAt
    //optional field - text, likes, retweets, etc
    int id;
    int createdAt;
    
    public Tweet(int id, int createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }
}

class Twitter {
    
    //map of user id to set of users that this id follows
    HashMap<Integer, HashSet<Integer>> followingNetwork; 
    //map of user id to list of tweets posted by that id
    HashMap<Integer, List<Tweet>> postedTweets;
    //timestamp to track recent tweets
    int timestamp;
    int FEED_SIZE;
    
    /** Initialize your data structure here. */
    public Twitter() {
        followingNetwork = new HashMap<>();
        postedTweets = new HashMap<>();
        timestamp = 0; //timestamp increases by 1 whenever an user tweets
        FEED_SIZE = 10;
    }
    
    //time - constant
    /** Compose a new tweet. */
    public void postTweet(int userId, int tweetId) {
        //create user if non existent
        if(!followingNetwork.containsKey(userId))
        {
            followingNetwork.put(userId, new HashSet<Integer>());
        }
        //logic - when a particular user tweets, he follows himself - done for ease of retreiving feed
        followingNetwork.get(userId).add(userId);
        //add the tweet in postedTweets map
        if(!postedTweets.containsKey(userId))
        {
            //handles non existent user
            postedTweets.put(userId, new ArrayList<Tweet>());
        }
        //add tweet to the corresponding list
        postedTweets.get(userId).add(new Tweet(tweetId, timestamp++));
        return;
    }
    
    //time - O(number of users * number of tweets posted * log(feed size))
    //space - O(feed size)
    /** Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent. */
    public List<Integer> getNewsFeed(int userId) {
        List<Integer> result = new ArrayList<>();
        //pq tracks most recent tweets - min heap based on createdAt coulmn of tweet object
        PriorityQueue<Tweet> pq = new PriorityQueue<>((a, b) -> a.createdAt - b.createdAt);
        //returns list of people whom userId follows
        HashSet<Integer> following = followingNetwork.get(userId);
        if(following != null)
        {
            for(Integer person : following)
            {
                //for each person followed by userId, get the list of posted tweets
                List<Tweet> tweets = postedTweets.get(person);
                if(tweets != null)
                {
                    //add the tweets to pq (max heap based on created at)
                    for(Tweet tweet : tweets) 
                    {
                        if(pq.size() < FEED_SIZE) //add if feed size is not yet reached
                        {
                            pq.offer(tweet);
                        }
                        else //else add if the current tweet is more recent than tweet at root of pq
                        {
                            if(pq.peek().createdAt < tweet.createdAt)
                            {
                                pq.poll();
                                pq.offer(tweet);
                            }
                        }
                    }
                }
            }
        }
        //add 10 most recent tweets into result and return
        while(!pq.isEmpty())
        {
            //add tweets to result and reverse the result before returning
            result.add(0, pq.poll().id);
        }
        return result;
    }
    
    //time - constant
    /** Follower follows a followee. If the operation is invalid, it should be a no-op. */
    public void follow(int followerId, int followeeId) {
        //create user if non existent
        if(!followingNetwork.containsKey(followerId))
        {
            followingNetwork.put(followerId, new HashSet<Integer>());
        }
        //add followee to set of users followed by followerId
        followingNetwork.get(followerId).add(followeeId);
        return;
    }
    
    //time - constant
    /** Follower unfollows a followee. If the operation is invalid, it should be a no-op. */
    public void unfollow(int followerId, int followeeId) {
        //user cant unfollow himself - breaches logic of getting feed
        if(followerId == followeeId)
        {
            return;
        }
        //confirm that followerId is already present
        if(followingNetwork.containsKey(followerId))
        {
            followingNetwork.get(followerId).remove(followeeId);
        }
        return;
    }
}

/**
 * Your Twitter object will be instantiated and called as such:
 * Twitter obj = new Twitter();
 * obj.postTweet(userId,tweetId);
 * List<Integer> param_2 = obj.getNewsFeed(userId);
 * obj.follow(followerId,followeeId);
 * obj.unfollow(followerId,followeeId);
 */
