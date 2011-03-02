class WelcomeController < ApplicationController
  include_class "pl.softwaremill.demo.HelloWorld"

  def index
    @rails_msg = "Hello from Rails!"
    @cdi_msg = inject(HelloWorld).message
  end
end
