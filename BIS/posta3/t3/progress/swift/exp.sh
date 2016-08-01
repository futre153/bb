#!/usr/bin/ksh


#meno spustaneho skriptu
SCRIPT_NAME=`basename $0`
SCRIPT_DIR=`dirname $0`

#LOG directory
LOG_MAIN=${SCRIPT_DIR}/log/log_exp.log

# VYTVORENIE PROSTREDIA
#whoami >> $LOG_MAIN

#echo "PATH : $PATH" >> $LOG_MAIN
#echo "DLC : $DLC" >> $LOG_MAIN
#echo "PROPATH : $PROPATH" >> $LOG_MAIN

#DLC=/progress/DLC/v91D; export DLC
#export PATH=$DLC/bin:$PATH:/usr/local/bin:/progress/PB/work91/system

#PROTERMCAP=$DLC/protermcap; export PROTERMCAP

#PROCFG=$DLC/progress.cfg
#PROMSGS=$DLC/promsgs; export PROMSGS
#PROOIDRV=$DLC/bin/_prooidv; export PROOIDRV
#PROOIBRK=$DLC/bin/_prooibk; export PROOIBRK
#PROAPSV=$DLC/bin/_proapsv;export PROAPSV
#PROAPBK=$DLC/bin/_proapbk;export PROAPBK
#PROSRV=$DLC/bin/_mprosrv; export PROSRV

#PROPATH=.:/progress/PB/work91/src; export PROPATH
# KONIEC VYTVORENIA PROSTREDIA
#        MIESTO TO VOLA V CRON UROBIT NASLEDOVNE
# su - <user> -c <exp.sh>
#
#==========================================

#===========================14. 7. 2005 10:33===============
# FUNCTIONS
#==========================================
cas() 
{
 date +"%d.%m.%Y %H:%M:%S"
}


timestamp()
{
  date +"%Y%m%d%H%M%S"  
}

watchdog() 
{ 
  #$1=PID $2=Time out
  #begin_t=$SECONDS
  while [ `ps -p $1 | wc -l` -ge 2 ]       
  do
    if [ $2 -ne 0 ]
      then
        sleep $2
    fi
   # actual=$SECONDS
   # let diff=$actual-$begin_t
   # if [ $diff -ge $2 ]                  
   # then 
   #    kill -9 $1 && check_error 12 $2  
   # fi
  done
}

#==========================================
# MAIN
#==========================================

cd ${SCRIPT_DIR}
#check whether script already running
POM=`ps -el -o args | grep -c '.* su\ .*'$SCRIPT_NAME`
if [ $POM -gt 1 ] 
  then 
    echo "`cas` EXPORT INFO Script $SCRIPT_NAME je úž spustený" >> $LOG_MAIN
    exit
fi

# v timer.txt je timestap odked ktoreho sa ma vykonat najblizsi import
[ -f timer_exp.txt ] && if [ "`cat timer_exp.txt`" -gt "`timestamp`" ] ; then exit ;fi    
#[ -f timer_exp.txt ] || 
echo "`timestamp`" > timer_exp.txt
chmod 660 timer_exp.txt

# nastav novy timer_imp
[ -f logexp.tmp ] && rm logexp.tmp
mbpro -pf run_asset.pf -p runp.p -param swsetime.p,ccc,EXPORT,timer_exp.txt,logexp.tmp,,$$ >/dev/null
 PID=`ps -el -o pid -o args | grep '.*swsetime.p\ .*'$$ | grep -v 'grep' | awk '{print $1}'`
 [ ! -z "$PID" ] && watchdog "$PID" 0
if [ -s logexp.tmp ]
  then 
    cat logexp.tmp >> $LOG_MAIN
    exit
fi

echo "`cas` EXPORT START" >> $LOG_MAIN

# Export SWIFT-ovych sprav do bazy
echo "`cas` EXPORT Suborov z bazy" >> $LOG_MAIN
[ -f logexp.tmp ] && rm log.tmp
mbpro -pf run_asset.pf -p runp.p -param msg2sw.p,cc,EXPORT,logexp.tmp,,$$ >/dev/null
 PID=`ps -el -o pid -o args | grep '.*msg2sw.p\ .*'$$ | grep -v 'grep' | awk '{print $1}'`
 [ ! -z "$PID" ] && watchdog "$PID" 10
if [ -s logexp.tmp ]
  then 
    cat logexp.tmp >> $LOG_MAIN
    echo "`cas` EXPORT STOP" >> $LOG_MAIN
    echo "=" >> $LOG_MAIN
    exit
fi

echo "`cas` EXPORT Prenos suborov na SWIFT ALLIANCE terminal" >> $LOG_MAIN
#swift_exp.sh ./exp~log.txt 172.16.51.163~c:/swift/expa DEBUG
/t3/progress/swift/swiftexp.sh `cat paramexp.txt`
if [ "$?" -ne "0" ]
  then
    echo "`cas` EXPORT ERROR pri prenose suborov na SWIFT ALLIANCE terminal" >> $LOG_MAIN
    echo "`cas` EXPORT STOP" >> $LOG_MAIN
    echo "=" >> $LOG_MAIN
    exit
fi

echo "`cas` IMPORT STOP" >> $LOG_MAIN
echo "=" >> $LOG_MAIN

