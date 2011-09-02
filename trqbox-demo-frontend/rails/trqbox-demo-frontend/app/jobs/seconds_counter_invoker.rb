class SecondsCounterInvoker
  #include InjectHelper

  #include_class "pl.softwaremill.demo.SecondsCounter"

  def run()
    #lookup(SecondsCounter).inc
  end
end