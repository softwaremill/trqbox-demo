class TwitterChecker
  include InjectHelper

  include_class "pl.softwaremill.demo.TwitterClient"

  attr_accessor :log

  def run()
    twitter = inject(TwitterClient)

    twitter.load_tweets

    log.info("Tweets loaded")

    twitter.load_friends

    log.info("Friends loaded")

    twitter.load_search

    log.info("Search loaded")
  end
end