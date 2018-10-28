EXE_FILE=./target/password-manager-1.0-SNAPSHOT-jar-with-dependencies.jar

if [ ! -f $EXE_FILE ]; then
    mvn package
    if [ ! -f $EXE_FILE ]; then
        echo "Cannot start due to a build error."
    else
        java -jar $EXE_FILE
    fi
else
    clear
    java -jar $EXE_FILE
fi
