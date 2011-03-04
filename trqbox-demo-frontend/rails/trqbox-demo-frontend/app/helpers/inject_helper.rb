module InjectHelper
  include_class "pl.softwaremill.common.util.dependency.D"

  def inject(what)
    D.inject(what.java_class)
  end
end