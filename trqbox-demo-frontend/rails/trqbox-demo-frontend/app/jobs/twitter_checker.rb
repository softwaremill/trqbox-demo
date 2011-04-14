class TwitterChecker
  include InjectHelper

  include_class "pl.softwaremill.demo.TwitterClient"

  def run()
    twitter = lookup(TwitterClient)

    twitter.load_tweets

    twitter.load_friends

    twitter.load_search

    puts("Twitter updated")
  end
end