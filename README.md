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

`trqbox-demo-ror-cdi-int` - the integration layer. The packaging is defined as `war`, but the war is never deployed.
 It is created only in order to have all the jars with their dependencies in one directory. These jars are then copied
 to `trqbox-demo-frontend/rails/trqbox-demo-frontend/lib` (think of this as `WEB-INF/lib`), and become part of the
 deployed Rails application (`.knob`). The knob also contains a `web.xml` file, in the `config` directory, specifying
 the listener, which setups `D`.

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
1. Deploy the backend and frontend: run the `deploy.sh` script (it copies the jars into the lib directory of the Rails
 app, and invokes a Torquebox Rake task which copies the `.knob` into JBoss's deploy directory).
1. Test that it works: go to `http://localhost:8080/welcome/index`. You should see welcome messages, coming both
 from Rails and from the CDI bean.

## Creating this application from scratch

### Setting up the project
1. Download & unzip latest (Torquebox)[http://torquebox.org/torquebox-dev.zip]
1. Create a maven project with three modules (backend, frontend, integration). The backend needs JEE6 APIs, the
 integration module should depend on the backend (so that the jar is included) an
1. Create a `conf_env` and source it (a convenience script - so that the proper path to Torquebox and JRuby is set)

### Creating an empty rails project
1. Install rails: `jruby -S gem install rails`
1. Create a `trqbox-demo-frontend/rails` directory, change to it
1. Create an empty Rails project: `jruby -S rails new trqbox-demo-frontend -m $TORQUEBOX_HOME/share/rails/template.rb`
 The project will have the name `trqbox-demo-frontend` and uses the Torquebox template, which adds some additional
 gems for jobs, scheduling, etc. (JBoss AS <-> JRuby/Rails integration).
1. Install any dependencies of the new project: `cd trqbox-demo-frontend; jruby -S bundle install`
1. Deploy the application: basically run `jruby -S rake torquebox:deploy` in the
 `trqbox-demo-frontend/rails/trqbox-demo-frontend` directory. This will generate a `trqbox-demo-frontend-knob.yml` file
 in `jboss/server/default/deploy`. Look inside it - nothing complicated there :). To redeploy the application, you just
 need to touch that file.
1. Open up `http://localhost:8080` - you should see the welcome screen.
1. Generate a controller (using the Rails generator), try adding some methods. No need to redeploy, the changes are
 visible instantly! :)

### Adding CDI integration
1. Create a `ServletContextListener` in the integration module which setups `D`
1. Add a `web.xml` with that listener to `trqbox-demo-frontend/rails/trqbox-demo-frontend/config`. This `web.xml` will
 become a part of the deployed knob.
1. Create a CDI bean in the `backend` module
1. `mvn clean install`. This will cause the integration module to produce an unpacked war with all the dependencies.
1. Now we need to copy those dependencies to `trqbox-demo-frontend/rails/trqbox-demo-frontend/lib` - create a script.
1. Finally we can lookup the dependency in Rails using `D` (see above)
1. Redeploy and test