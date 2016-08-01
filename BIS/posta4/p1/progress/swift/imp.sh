#!/usr/bin/ksh


#meno spustaneho skriptu
SCRIPT_NAME=`basename $0`
SCRIPT_DIR=`dirname $0`

#LOG directory
LOG_MAIN=${SCRIPT_DIR}/log/log_imp.log
#pridane pre potreby IBAN Plus inportu. Zadany log pre IBAN Plus (Brandys 19.04.2016)
LOG_MAIN_IBAN=${SCRIPT_DIR}/log/log_iban_imp.log

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
# su - <user> -c <imp.sh>
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
    echo "`cas` IMPORT INFO Script $SCRIPT_NAME je úž spustený" >> $LOG_MAIN
    #pridane pre potreby IBAN (Brandys 19.04.2016)
	echo "`cas` IBANE IMPORT INFO Script $SCRIPT_NAME je úž spustený" >> $LOG_MAIN_IBAN
    exit
fi


# v timer.txt je timestap odked ktoreho sa ma vykonat najblizsi import
[ -f timer_imp.txt ] && if [ "`cat timer_imp.txt`" -gt "`timestamp`" ] ; then exit ;fi    
#[ -f timer_imp.txt ] || 
echo "`timestamp`" > timer_imp.txt
chmod 660 timer_imp.txt

# nastav novy timer_imp
[ -f logimp.tmp ] && rm logimp.tmp
mbpro -pf run_asset.pf -p runp.p -param swsetime.p,ccc,IMPORT,timer_imp.txt,logimp.tmp,,$$ >/dev/null
 PID=`ps -el -o pid -o args | grep '.*swsetime.p\ .*'$$ | grep -v 'grep' | awk '{print $1}'`
 [ ! -z "$PID" ] && watchdog "$PID" 0
if [ -s logimp.tmp ]
  then 
    cat logimp.tmp >> $LOG_MAIN
    #pridane pre potreby IBAN (Brandys 19.04.2016)
	cat logimp.tmp >> $LOG_MAIN_IBAN
    exit
fi

echo "`cas` IMPORT START" >> $LOG_MAIN
#pridane pre potreby IBAN (Brandys 19.04.2016)
echo "`cas` IBAN IMPORT START" >> $LOG_MAIN_IBAN

echo "`cas` IMPORT Prenos suborov zo SWIFT ALLIANCE terminalu" >> $LOG_MAIN
swiftimp.sh `cat paramimp.txt`
#swift_imp.sh ./imp~log.txt 172.16.51.163~c:/swift/imp
if [ "$?" -ne "0" ]
  then
    echo "`cas` IMPORT ERROR pri prenose suborov zo SWIFT ALLIANCE terminalu" >> $LOG_MAIN
    echo "`cas` IMPORT STOP" >> $LOG_MAIN
    echo "=" >> $LOG_MAIN
    #pridane pre potreby IBAN (Brandys 19.04.2016)
	echo "`cas` IBAN IMPORT ERROR pri prenose suborov zo SWIFT ALLIANCE terminalu" >> $LOG_MAIN_IBAN
    echo "`cas` IBANE IMPORT STOP" >> $LOG_MAIN_IBAN
	echo "=" >> $LOG_MAIN_IBAN
	#koniec zmeny
    exit
fi

# Import SWIFT-ovych sprav do bazy
echo "`cas` IMPORT Suborov do bazy" >> $LOG_MAIN
[ -f logimp.tmp ] && rm logimp.tmp
mbpro -pf run_asset.pf -p runp.p -param sw2msg.p,cc,IMPORT,logimp.tmp,,$$ >/dev/null
 PID=`ps -el -o pid -o args | grep '.*sw2msg.p\ .*'$$ | grep -v 'grep' | awk '{print $1}'`
 [ ! -z "$PID" ] && watchdog "$PID" 10
if [ -s logimp.tmp ]
  then 
    cat logimp.tmp >> $LOG_MAIN
fi
echo "`cas` IMPORT STOP" >> $LOG_MAIN
echo "=" >> $LOG_MAIN

# pridane pre potreby IBAN Plus importu (Brandys 19.04.2016)
# pokial vynikne pri importe swiftovych sprav chyba zastavi sa aj import IBAN plus

swift_iban_imp_str.sh `cat param_iban_imp_str.txt`
if [ "$?" -ne "0" ]
  then
    echo "`cas` IBAN IMPORT ERROR pri prenose IBAN STRUCTURE suborov zo SWIFT ALLIANCE terminalu" >> $LOG_MAIN_IBAN
    echo "`cas` IBAN IMPORT pokracuje na IBAN PLUS" >> $LOG_MAIN_IBAN
fi
swift_iban_imp.sh `cat param_iban_imp.txt`
if [ "$?" -ne "0" ]
  then
    echo "`cas` IBAN IMPORT ERROR pri prenose IBAN PLUS suborov zo SWIFT ALLIANCE terminalu" >> $LOG_MAIN_IBAN
    echo "`cas` IBAN IMPORT pokracuje na IBAN EXCLUSIONS" >> $LOG_MAIN_IBAN
fi
swift_iban_imp_exc.sh `cat param_iban_imp_exc.txt`
if [ "$?" -ne "0" ]
  then
    echo "`cas` IBAN IMPORT ERROR pri prenose IBAN PLUS suborov zo SWIFT ALLIANCE terminalu" >> $LOG_MAIN_IBAN
fi
echo "`cas` IBAN IMPORT STOP" >> $LOG_MAIN_IBAN
echo "=" >> $LOG_MAIN_IBAN
#koniec zmeny