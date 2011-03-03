# Torquebox + CDI demo

## Project structure

`trqbox-demo-backend` - contains the "business logic" - the CDI beans and other java classes. The archive must
 contain a `beans.xml` file. Here there's only one bean, `HelloWorld`.

`trqbox-demo-frontend` - contains the frontend in Ruby On Rails. In the `rails` directory there's an application
 generated using Rails, with an additional controller (`welcome_controller`), which lookups a CDI bean from the backend
 and displays the result of a method.

The lookup is done in the following way:

    result = inject(MyJavaBean).some_method(param1, param2)

Where the inject method is simply:

    include_class "pl.softwaremill.common.util.dependency.D"

    def inject(what)
        D.inject(what.java_class)
    end

The `D` class comes from
 [Softwaremill util](https://github.com/softwaremill/softwaremill-common/tree/master/softwaremill-util/),
 a module of
 [Softwaremill common](https://github.com/softwaremill/softwaremill-common),
 and is a simple utility class for setting up (testable) static lookups of dependencies.

`trqbox-demo-ror-cdi-int` - the integration layer. Currently this is a `.war` which contains the backend and all
 dependencies. It is deployed and it's contents placed in the default classloader domain (see `jboss-classloading.xml`).
 The frontend must be deployed after this war; we specify this by `alises.txt` in `-int` and `jboss-depeendency.xml` in
 `-frontend`. Also, it contains a servlet listener which setups static lookups (using `D`) and is specified in the
 `web.xml` for `-frontend`, which is placed in the `/config` directory.

In the future I hope to make the integration much nicer by deploying the jars straight into the rails app, but this
 doesn't work yet.

## Deploying the example

1. Clone this repository
1. [Download latest Torquebox](http://torquebox.org/torquebox-dev.zip)
1. Unzip it next to the repository
1. Create a `conf_env` file basing on `conf_env_template`. There you need to fill in the `TORQUEBOX_HOME` property,
 which should be the directory that got created by unpacking Torquebox.
1. Install Bundler: first `source conf_env`, then `jruby -S gem install bundler`. This is needed because Torquebox comes
 with an "empty" JRuby installation.
1. Install dependencies: go to `trqbox-demo-frontend/rails/trqbox-demo-frontend` and run: `jruby -S bundle install`.
 This should install Rails.
1. Build the project: `mvn clean install`
1. Now start up the AS: go to `$TORQUEBOX_HOME/jboss/bin` and execute `run.sh`.
1. Deploy the backend and frontend: run the `deploy.sh` script (it copies the `.war` into JBoss's deploy directory,
 and invokes a Torquebox Rake task which copies the `.knob` into the same place).
1. Test that it works: go to `http://localhost:8080/welcome/index`. You should see welcome messages, coming both
 from Rails and from the CDI bean.