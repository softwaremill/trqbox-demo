class SecondsCounterInvoker
  include InjectHelper

  include_class "pl.softwaremill.demo.SecondsCounter"

  attr_accessor :log

  def run()
    inject(SecondsCounter).inc
    log.info("Done")
  end
end