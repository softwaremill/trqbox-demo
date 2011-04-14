class WelcomeController < ApplicationController
  include_class "pl.softwaremill.demo.HelloWorld"
  include_class "pl.softwaremill.demo.TwitterClient"

  def index
    @rails_msg = "Hello from Rails!"
    @cdi_msg = lookup(HelloWorld).message
  end

  def followers
    twitter = lookup(TwitterClient)

    @twitter_followers = twitter.get_users
  end

  def search
    twitter = lookup(TwitterClient)

    @twitter_search = twitter.get_search
  end

  def tweets
    twitter = lookup(TwitterClient)

    @twitter_tweets = twitter.get_statuses
  end
end
