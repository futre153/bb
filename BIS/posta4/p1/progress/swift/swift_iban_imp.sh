#!/usr/bin/ksh


#set -x

############################################################################## 
# 
#SCRIPT: swift_iban_imp.sh                                                          
# 
#DATE: 20.06.2005                                                               
#
#AUTHOR: Juraj Riska                                                         
#
#VERION 1.0                                                                    
#
#
#PURPOSE : 
#  Tento skripkt vykonáva prenos súborov zo SWIFT terminalu na SERVER BIS  
# 
#  prvy  parameter : LOCALNY_ADRESAR~LOG_SUBOR                         
#
#  druhy parameter : SERVER~SWIFT_ADRESAR                   
#
##############################################################################



#Navratovy kod Connection Timeout
typeset -i R_CODE=0 

#Timeout
typeset -i TO=60 TO_SSH=60 TO_SCP=60

#Velkost logovacieho suboru
typeset -i LOG_SIZE_LIMIT=50000

#konto uzivatela na SWIFT terminali
S_USER="swifter"

#konto ktore spusta proces cez CRON
U_PBSWIFT="swiftftp"
#grupa do ktorej patria <U_PBSWIFT> aj <U_PBBIS>
G_PBSWIFT="progress"
#konto cez ktore bezi appserver
U_PBBIS="padm"

#Zapnuty debug mod ak treti parameter je DEBUG
S_DEBUG="$3"

#Maska importovanych suborov
#Zmena oproti swiftimp.sh, maska upravená pre potreby IBAN Directory Plus (Brandys, 19.04.2016)
#Pôvodná hodnota:MASKA="[Ii0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][_][2][0][0-9][0-9][01][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9].[TtOo][XxUu][Tt]"
#Maske pre IBAN Directory Plus daily delta XML file
MASKA="[I][B][A][N][P][L][U][S][_][V][0-9][_][D][A][I][L][Y]_[D][E][L][T][A][_][2][0][0-9][0-9][01][0-9][0-3][0-9].[Xx][Mm][Ll]"

#meno spustaneho skriptu
SCRIPT_NAME=`basename $0`

#Local side
L_ADR=`echo $1 | cut -d \~ -f1 | sed 's#/\$##g'`
LOG_SUB=`echo $1 | cut -d \~ -f2` # log subor pre jeden beh po uspesnom dokonceni obsahuje zoznam prenesenych suborov

#Remote side
SERV=`echo $2 | cut -d \~ -f1`
R_ADR=`echo $2 | cut -d \~ -f2 | sed 's#/\$##g'`

#pracovny subor do ktoreho sa loguje vystup jednotlivych prikazov skriptu a v pripade potreby sa nakopiruje do LOG_SUB
#Zmena oproti swiftimp.h, meno doèasného súboru bude upravené pre potreby IBAN Directory Plus (Brandys, 19.04.2016)
#Pôvodná hodnota:TMP=${L_ADR}/tmp/tran_imp_tmp.$$
TMP=${L_ADR}/tmp/tran_iban_imp_tmp.$$
#LOG subor do ktoreho sa loguje aktivita skriptu swift_imp.sh
#Zmena oproti swiftimp.h, meno log súboru bude upravené pre potreby IBAN Directory Plus (Brandys, 19.04.2016)
#Pôvodná hodnota:SUM_LOG=${L_ADR}/log/tran_imp.log
SUM_LOG=${L_ADR}/log/tran_iban_imp.log

#pridane Branislav Brandys, premenne pre flag subor
FLAG_FILE=${L_ADR}/../work/uzav/bis_in/ibanplus_sts.txt

LOG_SUB=${L_ADR}/${LOG_SUB}
#Zmena oproti swiftimp.h, cie¾ový adresár bude upravené pre potreby IBAN Directory Plus (Brandys, 19.04.2016)
#Pôvodná hodnota:L_ADR=${L_ADR}/fromswift
L_ADR=${L_ADR}/../work/uzav/bis_in/ibanplus/davky


##############################################################################
#                                      FUNCIONS                                
#
##############################################################################
 

cas()
{
 date +"%d.%m.%Y %H:%M:%S"
}


timestamp()
{
 date +"%Y%m%d%H%M%S"  
}


rm_tmp()
{
 [ -f $TMP ] && rm -f $TMP
}


check_error() 
{ 
 #echo "$1 $2"
 case $1 in

        1)
         echo "`cas` IMPORT ERROR `cat $TMP`"
         [ $R_CODE -eq 0 ] && R_CODE=1 ;;
        2)
         echo "`cas` IMPORT ERROR Md5sum for $2 doesn't match"
         [ $R_CODE -eq 0 ] && R_CODE=2 ;;
        3)
         echo "`cas` IMPORT WARNING File $2 already exist on local side" ;;
        4)
         echo "`cas` IMPORT INFO File $2 has been transfered correctly" ;;
        5)
         echo "`cas` IMPORT INFO No file is suitable for transfer" ;;

#####Check INPUT parameters >>
        7)
          echo "`cas` IMPORT ERROR Local directory $2 doesn't exist" | tee $LOG_SUB 
          R_CODE=7 ;;
        8)
          #can't write to local directory
          echo "`cas` IMPORT ERROR Can't write to local directory $2" | tee $LOG_SUB 
          rm_tmp
          R_CODE=8 ;; 
        9)
          if [ "`cat $TMP | awk '{print $(NF-1),$NF}'`" = "not found." ]
          then 
               echo "`cas` IMPORT ERROR Remote direcory $2 doesn't exist" | tee $LOG_SUB 
          else 
              echo "`cas` IMPORT ERROR `cat $TMP`" | tee $LOG_SUB
          fi
          rm_tmp
          R_CODE=9 ;; 
       10)
          echo "`cas` IMPORT ERROR Number of parameter must be equal 2" | tee $LOG_SUB
          R_CODE=10 ;;
       11)
          echo "`cas` IMPORT ERROR Script $SCRIPT_NAME already running" | tee $LOG_SUB
          R_CODE=11 ;;
#####Check INPUT parameters <<

        12)
          echo "`cas` IMPORT ERROR LINK DOWN ssh process has been killed"
          [ $R_CODE -eq 0 ] && R_CODE=12
          ;;
#Zmena oproti swiftimp.h, pridan0 stavz pre potreby IBAN Directory Plus (Brandys, 20.01.2016)
		13)
		 echo "`cas` IBAN IMPORT ERROR FILE $FLAG_FILE does not exists or is not writable"
		 R_CODE=13 ;;
		14)
		 echo "`cas` IBAN IMPORT WARNING: import ibanplus allready activated"
		 R_CODE=14 ;;
		15)
		 echo "`cas` IBAN IMPORT ERROR: unknown flag value ($1)"
		 R_CODE=15 ;;
# koniec zmeny
        *)
         echo "`cas` IMPORT ERROR Undefined error"
         R_CODE=20 ;;
 
 esac >> $SUM_LOG
}


watchdog() 

{ 
  #$1=PID $2=Time out
  begin_t=$SECONDS
  while [ `ps -p $1 | wc -l` -ge 2 ]       
  do
    actual=$SECONDS
    let diff=$actual-$begin_t
    if [ $diff -ge $2 ]                  
    then 
       kill -9 $1 && check_error 12 $2  
    fi
  done
}


ssh_run() 
{
 ssh -o Batchmode=yes $* &
 PID=$!
 watchdog $PID $TO_SSH   
} 


scp_run() 
{
 scp -B $* &
 PID=$!
 watchdog $PID $TO_SCP
}


check_params() 
{
#L_ADR LOG_SUB SERV R_ADR

 if [ $# -eq 4 ]
 then
  if [ -d $1 ]                                                                                                                 # existuje adresar
  then
    touch $2 2>$TMP || check_error 8 $1   # ak nie je modifikovatelky
  else
    check_error 7 $1                                                                                         # neexistuje adresar
  fi   

  ssh_run ${S_USER}@${3} cd $4 2>$TMP                 # overenie pristupu do vzialeneho adresara
  [ -s $TMP ] && check_error 9 $4         # ak nastala chyba vypisem chybu
      
 else
   check_error 10                                                                                                 # pocet parametrov nie je 4
 fi
}


set_acl() 
{
  #setacl -u user:pbswift:rw-,group:pbswift:rw-,other::---,user:pbbis:rw- $1 2>$TMP
  #setacl -u user:${U_PBSWIFT}:rw-,group:${G_PBSWIFT}:rw-,other::---,user:${U_PB~ BIS}:rw- $1 2>$TMP
 chmod 660 $1 2>$TMP
}
 

log_rotation()
{
 if [ -f $SUM_LOG ]   
 then
    typeset -i SIZE=`ls -al $SUM_LOG 2>/dev/null | awk '{print $5}'`
    if [ $SIZE -ge $LOG_SIZE_LIMIT ]  
    then
       DS="$SUM_LOG.`timestamp`"
       cp $SUM_LOG $DS 
       cp /dev/null $SUM_LOG
    fi
 fi
}


###############################################################################
#                               Beginig of MAIN                               #
###############################################################################


#check whether script already running
POM=`ps -el -o args | grep $SCRIPT_NAME | grep -v grep | grep -c $SCRIPT_NAME`
if [ $POM -gt 1 ] 
  then check_error 11;exit $R_CODE
fi

#pridane Branislav Brandys 19.04.2016, kontrola flag suboru 
if [ -f ${FLAG_FILE} ] && [ -w ${FLAG_FILE} ]
then
	IBAN_FLAG=`cat ${FLAG_FILE}`
	case ${IBAN_FLAG} in
		0)
		;;
		1)
		check_error 14;exit $R_CODE ;;
		*)
		check_error 15;exit $R_CODE ;;
	esac
else
	check_error 13;exit $R_CODE
fi
#koniec zmeny

#check input parameters 
if [ "$S_DEBUG" = "DEBUG" ]
then
  echo "check_input"
  echo "L_ADR   : $L_ADR"
  echo "LOG_SUB : $LOG_SUB"
  echo "SERV    : $SERV"
  echo "R_ADR   : $R_ADR"
fi
check_params $L_ADR $LOG_SUB $SERV $R_ADR  
[ $R_CODE -ne 0 ] && exit $R_CODE

#zalozi prazdny subor
>$LOG_SUB 

#do LST nacita zoznam importovanych suborov zo SWIFT terminalu
LST=`ssh_run ${S_USER}@${SERV} ls -1 ${R_ADR}/${MASKA} 2>/dev/null`

if [ ! -z "$LST" ] # vypis nie je prazdny
then
  if [ "$S_DEBUG" = "DEBUG" ] ;then  echo "for i in ";fi
  for i in $LST
  do
    R_CODE=0 # pre kazdy prenesený subor nastavujem na zaciatku stav NO-ERROR
    #test ci subor uz existuje na lokali
    ii=`basename $i`                                                                                                 # len nazov suboru bez cesty
    if [ "$S_DEBUG" = "DEBUG" ] ;then  echo " for  ${ii}"; echo " ${i}"; fi
    L_ADR_F=${L_ADR}/${ii}                                                                         #Nazov suboru do ktoreho sa vykona kopirovanie aj s cestou
    CS="${S_USER}@${SERV}"                                                                         #konektovaci retazec pre ssh a scp user@server
    if [ "$S_DEBUG" = "DEBUG" ] ; then echo "$L_ADR_F"; fi
    if [ ! -f $L_ADR_F ]                                                                                 # nenasiel sa subor na lokali   
    then
      if [ "$S_DEBUG" = "DEBUG" ] ; then echo "copy file from remote side to local and rename it"; fi
      [ $R_CODE -eq 0 ] && scp_run ${CS}:${i} ${L_ADR_F}.TMP 2>$TMP #ak predtym nenastala chyba vòuskutocni sa preno
      [ -s $TMP ] && check_error 1 $ii
                
      if [ "$S_DEBUG" = "DEBUG" ] ; then echo "transfer looks ok"; fi
      if [ $R_CODE -eq 0 ]
      then  
        T1=`sum -r ${L_ADR_F}.TMP | awk '{print $1,$2}'`
        T2=`ssh_run $CS cmd /c "sum -r ${i}" | awk '{print $1,$2}'`
        if [ "$S_DEBUG" = "DEBUG" ]
        then
          echo "$T1"
          echo "$T2"
        fi
        if [ "$T1" = "$T2" ]
        then  
          if [ "$S_DEBUG" = "DEBUG" ] ; then echo "transfer OK rename it to previous"; fi
          [ $R_CODE -eq 0 ] && mv ${L_ADR_F}.TMP ${L_ADR_F} 2>$TMP || \
          check_error 1 $ii 
                 
          if [ "$S_DEBUG" = "DEBUG" ] ; then echo "change mod for file on local side "; fi
          [ $R_CODE -eq 0 ] && set_acl ${L_ADR_F} || \
          check_error 1 $ii
          
          if [ "$S_DEBUG" = "DEBUG" ]
          then   
            echo "add to transfered file on remote side ARCH suffix "
            echo "${i}"
            echo "${R_ADR}/ARCH/${ii}.ARCH"
          fi
          [ $R_CODE -eq 0 ] && \
            ssh_run $CS mv ${i} ${R_ADR}/ARCH/${ii}.ARCH 2>$TMP 
          [ -s $TMP ] && check_error 1 $ii

          if [ "$S_DEBUG" = "DEBUG" ] ; then echo "write successfully transfered file to log"; fi
          [ $R_CODE -eq 0 ] && check_error 4 $ii
        else 
          if [ "$S_DEBUG" = "DEBUG" ] ; then echo "error during the transfer md5sum doesn't match"; fi
          check_error 2 $ii 
        fi
      fi
    else
      if [ "$S_DEBUG" = "DEBUG" ] ; then echo "this file already exist on local side"; fi
      check_error 3 $ii
    fi 
  done
 #pridane pre ptreby importu iban plus Branislav Brandys (Brandys, 20.01.2016)
 if [ "${R_CODE}" == "0" ]
 then
	echo "1" > ${FLAG_FILE}
 fi
  # koniec zmeny
else 
  [ $R_CODE -eq 0 ] && check_error 5 
fi

ls -1 ${L_ADR}/${MASKA} 2>/dev/null | sed 's#^.*/##g' > $LOG_SUB 

#clean up

rm_tmp

#[ -f ${L_ADR}/*.TMP ] && rm -f ${L_ADR}/*.TMP 

log_rotation

return $R_CODE
