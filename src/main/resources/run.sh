#!/usr/bin/env bash
# Description:
#       Timanetworks Spring Cloud Boot Scripts.
#   @Date:    2017-11-23
#   @Version: 1.0
# History:
# 2017-12-13, modify scripts, such as log file,remove lib/bin etc..
# 2018-01-31, modify scripts, move check enviroment variable position, from 'init_env' to 'init_script'.
# Get base path
#set -x
BASEDIR=$(cd $(dirname $0); pwd -P)
SCRIPTNAME=$(basename $0)
LIB_HOME=$BASEDIR
APPNAME="bigdata-openapi"
JAVA_OPT="-Djava.rmi.server.hostname=10.0.200.38
	  -Dcom.sun.management.jmxremote
	  -Dcom.sun.management.jmxremote.port=1089
	  -Dcom.sun.management.jmxremote.authenticate=false
	  -Dcom.sun.management.jmxremote.ssl=false
	  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
          -Dio.netty.leakDetectionLevel=ADVANCED "

#define functions
#help info
Usage(){
    echo Usage:
    echo "$0 start|stop|restart|status"
    # echo "Extended Parameters:$Extended_Parameters"
}

# @info: init script
# @args：(none)
# @deps: (none)
# @return: (none)
init_script() {
     jarName=$(get_jarName)
    [[ $? = 1 ]] && echo "$jarName" && exit 1
    [[ -z ${MAX_WAIT_TIME} ]] && MAX_WAIT_TIME=60
    [[ -z ${JAVA_HOME} ]] && JAVA="java" || JAVA="${JAVA_HOME}/bin/java"
}


# @info:    get the name of jar in lib.
# @args:    (none)
# @deps:    (none)
# @return:  the name of jar.
get_jarName() {
        if [[ $(find ${LIB_HOME} -maxdepth 1 -name '*.jar' 2>/dev/null | wc -l) != 1 ]]; then
            echo "there must be one jar file in ${LIB_HOME}."
            return 1
        else
            local jarName=$(find ${LIB_HOME} -maxdepth 1 -name '*.jar' | xargs basename)
        echo "${jarName}"
            return 0
        fi
}

# @info:    get the pid of app.
# @args:    (none)
# @deps:    (none)
# @return:  pid. exit code: 1/2/0
get_appPid() {
    local matchCount=$(ps -ef | egrep -w "java .*\s+.*${jarName}\$" | wc -l)
    if [[ ${matchCount} > 1 ]]; then
        #there are more than one process match ${appName}
        return 2
    elif [[ ${matchCount} = 0 ]]; then
        #can't get the pid match ${appName}
        return 1
    else
        local pid=$(ps -ef | egrep -w "java .*\s+.*${jarName}\$" | awk '{print $2}')
        echo $pid
        return 0
    fi
}

# @info:    start app.
# @args:    (none)
# @deps:    init_env app_opts get_appPid
# @return:  0 for successful, 1 for failed.
start_app(){
    local app_pid
    app_pid=$(get_appPid)
    if [[ $? != 1 ]];then
        echo "${APPNAME} already running!(pid:${app_pid})"
        return 0
    fi

    # start now
    nohup $JAVA -Duser.timezone=GMT+08 -jar -Xms1024m -Xmx1024m -Xmn512m ${LIB_HOME}/${jarName} >/dev/null 2>&1 &
    #nohup java  ${JAVA_OPT} -jar ${LIB_HOME}/${jarName} >/dev/null 2>&1 &
    sleep 4
    app_pid=$(get_appPid)
    if [[ $? = 0 ]];then
        echo "${APPNAME} start successfully, running with pid: ${app_pid}."
        return 0
    else
        echo "can not find pid, failed to start ${APPNAME}."
        return 1
    fi
}

# @info:    stop app.
# @args:    (none)
# @deps:    get_appPid
# @return:  0 for successful, 1 for failed.
stop_app(){
    local app_pid
    app_pid=$(get_appPid)
    if [[ $? = 1 ]];then
        echo "${APPNAME} is not running!"
        return 0
    elif [[ $? = 2 ]]; then
        echo "there are more than one process match ${APPNAME}."
        return 1
    fi
    # stop now
    echo "stop ${APPNAME} now..."
    kill ${app_pid}

    local timer=0
    until [[ $(get_appPid 1>/dev/null 2>&1; echo $?) != 0 || $timer = ${MAX_WAIT_TIME} ]]; do
        sleep 1
        ((timer=$timer+1))
    done

    if [[ $timer = ${MAX_WAIT_TIME} ]]; then
        echo "stop ${APPNAME} failed after ${MAX_WAIT_TIME} seconds."
        echo "stop ${APPNAME} by force now."
        kill -9 ${app_pid}
    fi
    sleep 1
    app_pid=$(get_appPid)
    if [[ $? != 1 ]]; then
        echo "stop ${APPNAME} failed with pid: ${app_pid}."
        return 1
    else
        echo "stop ${APPNAME} successfully."
    fi
}

# @info:    check app running status.
# @args:    (none)
# @deps:    get_appPid
# @return:  the app running status info.
status_app() {
    local app_pid
    app_pid=$(get_appPid)
    if [[ $? = 0 ]];then
        echo "${APPNAME} is running with pid: ${app_pid}."
    elif [[ $? = 2 ]]; then
        echo "there are more than one process match ${APPNAME}."
    else
        echo "${APPNAME} is not running."
    fi
}


#end function define
#main
init_script

case "$1" in
start)
    start_app
    ;;
stop)
    stop_app
    ;;
restart)
    stop_app
    start_app
    ;;
status)
    status_app
    ;;
*)
    Usage
    exit 1
    ;;
esac
exit 0
