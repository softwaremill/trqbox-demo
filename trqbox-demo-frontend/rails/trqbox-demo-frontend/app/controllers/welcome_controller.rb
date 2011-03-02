class WelcomeController < ApplicationController
  include_class "pl.softwaremill.demo.HelloWorld"

  def index
    @rails_msg = "Hello from Rails!"
    puts "X #{inject(HelloWorld)}"
    #@cdi_msg = inject(HelloWorld).message
  end
end
