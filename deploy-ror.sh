#!/bin/bash
if [ "$1" = "--skip-config" ]; then
    echo "skip config"
    echo "TORQUEBOX_HOME = $TORQUEBOX_HOME"
    echo "PATH = $PATH"
else
    source conf_env
fi

export YML_FILE=$TORQUEBOX_HOME/apps/trqbox-demo-frontend-knob.yml
if [ -e $YML_FILE ]; then
	echo "Redeploy, touching"
	touch $YML_FILE
else
	echo "First time deploy, running rake"
	cd trqbox-demo-frontend/rails/trqbox-demo-frontend
	jruby -S rake torquebox:deploy
fi
