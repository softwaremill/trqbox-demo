module InjectHelper
  include_class "pl.softwaremill.common.util.dependency.D"

  def lookup(what)
    D.inject(what.java_class)
  end
end