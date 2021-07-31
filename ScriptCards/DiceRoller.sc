!script {{
--:|__DiceRoller_0.1
  
  --:TOP|
  --#reentrant|DiceRoller
  --#title|Dice Roller 
  --#titleCardBackground|#000000
  --#oddRowBackground|#000000
  --#evenRowBackground|#000000
  --#buttonbackground|#000000
  --#buttontextcolor|#FFFFFF
  --#buttonbordercolor|#000000
  --#evenRowFontColor|#FFFFFF
  --#oddRowFontColor|#FFFFFF
  --#bodyFontSize|12px
  --#hidecard|0    
  --#whisper|self
  --#debug|1
  --#leftsub|
  --#rightsub|

  --#dicefontcolor|#FFFFFF
  --#dicefontsize|40px
  --#usehollowdice|0

    --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed white;"
    --&trStyle1|style="border-top:0px dashed white;"
    --&trStyle2|style="border:0px dashed white;"
    --&tdStyle1|style="width:20%;text-align:center;background-color:#000000;font-size:100%"
    --&tdStyle2|style="width:40%;text-align:center;background-color:#000000;font-size:100%"
    --&tdStyle3|style="width:10%;text-align:right;background-color:#000000;font-size:100%"
  
    --+|[t [&tStyle]]
          [tr [&trStyle1]]
            [td [&tdStyle1]]D20[/td] [td [&tdstyle2]] Advantage [/td] [td [&tdstyle2]] Disadvantage [/td]
          [/tr]
          [tr [&trStyle1]]
            [td [&tdStyle1]][rbutton][d20]20[/d20]::GETADDER;20\1[/rbutton][/td]
            [td [&tdstyle2]][rbutton][d20]20[/d20][d20]20[/d20]::GETADDER;ADV\1[/rbutton][/td] 
            [td [&tdstyle2]][rbutton][d20]20[/d20][d20]20[/d20]::GETADDER;DISADV\1[/rbutton][/td]
          [/tr]
        [/t]

    --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle3]] [d4]1[/d4][/td] 
            [td [&tdStyle3]][rbutton]1::GETADDER;4\1[/rbutton][/td]
            [td [&tdStyle3]][rbutton]2::GETADDER;4\2[/rbutton][/td]
            [td [&tdStyle3]][rbutton]3::GETADDER;4\3[/rbutton][/td]
            [td [&tdStyle3]][rbutton]4::GETADDER;4\4[/rbutton][/td]
            [td [&tdStyle3]][rbutton]5::GETADDER;4\5[/rbutton][/td]
            [td [&tdStyle3]][rbutton]6::GETADDER;4\6[/rbutton][/td]
            [td [&tdStyle3]][rbutton]7::GETADDER;4\7[/rbutton][/td]
            [td [&tdStyle3]][rbutton]8::GETADDER;4\8[/rbutton][/td]
          [/tr]
        [/t]

    --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle3]] [d6]1[/d6][/td] 
            [td [&tdStyle3]][rbutton]1::GETADDER;6\1[/rbutton][/td]
            [td [&tdStyle3]][rbutton]2::GETADDER;6\2[/rbutton][/td]
            [td [&tdStyle3]][rbutton]3::GETADDER;6\3[/rbutton][/td]
            [td [&tdStyle3]][rbutton]4::GETADDER;6\4[/rbutton][/td]
            [td [&tdStyle3]][rbutton]5::GETADDER;6\5[/rbutton][/td]
            [td [&tdStyle3]][rbutton]6::GETADDER;6\6[/rbutton][/td]
            [td [&tdStyle3]][rbutton]7::GETADDER;6\7[/rbutton][/td]
            [td [&tdStyle3]][rbutton]8::GETADDER;6\8[/rbutton][/td]
          [/tr]
        [/t]

    --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle3]] [d8]1[/d8][/td] 
            [td [&tdStyle3]][rbutton]1::GETADDER;8\1[/rbutton][/td]
            [td [&tdStyle3]][rbutton]2::GETADDER;8\2[/rbutton][/td]
            [td [&tdStyle3]][rbutton]3::GETADDER;8\3[/rbutton][/td]
            [td [&tdStyle3]][rbutton]4::GETADDER;8\4[/rbutton][/td]
            [td [&tdStyle3]][rbutton]5::GETADDER;8\5[/rbutton][/td]
            [td [&tdStyle3]][rbutton]6::GETADDER;8\6[/rbutton][/td]
            [td [&tdStyle3]][rbutton]7::GETADDER;8\7[/rbutton][/td]
            [td [&tdStyle3]][rbutton]8::GETADDER;8\8[/rbutton][/td]
          [/tr]
        [/t]

    --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle3]] [d10]1[/d10][/td] 
            [td [&tdStyle3]][rbutton]1::GETADDER;10\1[/rbutton][/td]
            [td [&tdStyle3]][rbutton]2::GETADDER;10\2[/rbutton][/td]
            [td [&tdStyle3]][rbutton]3::GETADDER;10\3[/rbutton][/td]
            [td [&tdStyle3]][rbutton]4::GETADDER;10\4[/rbutton][/td]
            [td [&tdStyle3]][rbutton]5::GETADDER;10\5[/rbutton][/td]
            [td [&tdStyle3]][rbutton]6::GETADDER;10\6[/rbutton][/td]
            [td [&tdStyle3]][rbutton]7::GETADDER;10\7[/rbutton][/td]
            [td [&tdStyle3]][rbutton]8::GETADDER;10\8[/rbutton][/td]
          [/tr]
        [/t]

    --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle3]] [d12]1[/d12][/td] 
            [td [&tdStyle3]][rbutton]1::GETADDER;12\1[/rbutton][/td]
            [td [&tdStyle3]][rbutton]2::GETADDER;12\2[/rbutton][/td]
            [td [&tdStyle3]][rbutton]3::GETADDER;12\3[/rbutton][/td]
            [td [&tdStyle3]][rbutton]4::GETADDER;12\4[/rbutton][/td]
            [td [&tdStyle3]][rbutton]5::GETADDER;12\5[/rbutton][/td]
            [td [&tdStyle3]][rbutton]6::GETADDER;12\6[/rbutton][/td]
            [td [&tdStyle3]][rbutton]7::GETADDER;12\7[/rbutton][/td]
            [td [&tdStyle3]][rbutton]8::GETADDER;12\8[/rbutton][/td]
          [/tr]
        [/t]


  --X|

 --:GETADDER|Case, Number
   --#hidecard|0
   --#Title|Dice Modfier
  --~Arg|string;split;\;[&reentryval]d
  --&Case|[&Arg1]
  --&Nbr|[&Arg2]

   --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed white;"
   --&trStyle1|style="border-top:0px dashed white;"
   --&trStyle2|style="border:0px dashed white;"
   --&tdStyle1|style="width:20%;text-align:left;background-color:#000000;font-size:80%"
   --&tdStyle2|style="width:30%;text-align:center;background-color:#000000;font-size:80%"
   --&tdStyle3|style="width:40%;text-align:right;background-color:#000000;font-size:80%"
 
   --+|[t [&tStyle]]
       [tr [&trStyle1]]
           [td [&tdStyle1]][rbutton]+1::ROLLDICE;[&reentryval]\1[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+2::ROLLDICE;[&reentryval]\2[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+3::ROLLDICE;[&reentryval]\3[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+4::ROLLDICE;[&reentryval]\4[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+5::ROLLDICE;[&reentryval]\5[/rbutton][/td] 
       [/tr]
       [tr [&trStyle1]]
           [td [&tdStyle1]][rbutton]+6::ROLLDICE;[&reentryval]\6[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+7::ROLLDICE;[&reentryval]\7[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+8::ROLLDICE;[&reentryval]\8[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+9::ROLLDICE;[&reentryval]\9[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]+10::ROLLDICE;[&reentryval]\10[/rbutton][/td] 
       [/tr]
       [tr [&trStyle1]]
           [td colspan=5 [&tdStyle1]][c][rbutton]None (0)::ROLLDICE;[&reentryval]\0[/rbutton][/c][/td]
       [/tr]
       [tr [&trStyle1]]
           [td [&tdStyle1]][rbutton]-1::ROLLDICE;[&reentryval]\-1[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-2::ROLLDICE;[&reentryval]\-2[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-3::ROLLDICE;[&reentryval]\-3[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-4::ROLLDICE;[&reentryval]\-4[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-5::ROLLDICE;[&reentryval]\-5[/rbutton][/td] 
       [/tr]
       [tr [&trStyle1]]
           [td [&tdStyle1]][rbutton]-6::ROLLDICE;[&reentryval]\-6[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-7::ROLLDICE;[&reentryval]\-7[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-8::ROLLDICE;[&reentryval]\-8[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-9::ROLLDICE;[&reentryval]\-9[/rbutton][/td] 
           [td [&tdStyle1]][rbutton]-10::ROLLDICE;[&reentryval]\-10[/rbutton][/td] 
       [/tr]
    [/t]
 
  --X|

 --:ROLLDICE|Dice, nbr, addr
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]
  --&Die|[&Arg1]
  --&Nbr|[&Arg2]
  --&Addr|[&Arg3]
  --#leftsub|[&Nbr]d[&Die] + ([&Addr])

  --?[&Die] -eq ADV|>ROLLADV;[&Addr]
  --?[&Die] -eq DISADV|>ROLLDISADV;[&Addr]

  --=RollCount|1 --=RollTotal|0
  --:RD_LOOPSTART|
  	--?[$RollCount.Raw] -gt [&Nbr] -or [$RollCount.Raw] -gt 100|RD_DONE
  	--=Res|1d[&Die]
  	--=RollTotal|[$RollTotal] + [$Res]
    --+&nbsp;[$RollCount]|&nbsp;&nbsp;[$Res]
    --=RollCount|[$RollCount] + 1
  	--^RD_LOOPSTART| 
  --:RD_DONE|

  --+SubTotal|[$RollTotal.Total]&nbsp;&nbsp; + &nbsp;&nbsp;Bonus([&Addr])
    --=RollTotal|[$RollTotal] + [&Addr]
  --+|[hr]
  --/|=Res|[&R]
  --=Avg|[$RollTotal.Total]/[&Nbr] --=Avg|[$Avg]*100\100
  --=Expected|[&Die] + 1 --=Expected|[&Nbr] * [$Expected] / 2 --=Expected|[$Expected] + [&Addr]
  --+&nbsp;Total|[$RollTotal.Total]&nbsp;&nbsp;[b]Exp:[/b] [$Expected]&nbsp;&nbsp;[b]Avg:[/b] [$Avg] 
  --#title|[$RollTotal.Raw]
  --+|[r][rbutton]Again::ROLLDICE;[&reentryval][/rbutton]&nbsp;&nbsp;[rbutton]Roll Dice::TOP[/rbutton][/r]
  --X|

 --:ROLLADV|
  --#hidecard|0
  --&Addr|[%1%]
  --#leftsub|Roll Advantage + ([&Addr])

  --=Res1|1d20 + [&Addr]
  --=Res2|1d20 + [&Addr]

  --+|[br]
  --+1st D20:|[$Res1]&nbsp;&nbsp;&nbsp;[b]2nd D20:[/b][$Res2]

  --~Res|math;max;[$Res1];[$Res2]

  --+|[hr]

  --+Result|[$Res.Total]
  --#title|[$Res.Raw] ([$Res1.Raw],[$Res2.Raw])

  --+|[r][rbutton]Again::ROLLADV[/rbutton]&nbsp;&nbsp;[rbutton]Roll Dice::TOP[/rbutton][/r]
  --x|

 --:ROLLDISADV|
  --#hidecard|0
  --&Addr|[%1%]
  --=Res1|1d20 + [&Addr]
  --=Res2|1d20 + [&Addr]
  --#leftsub|Roll Disadvantage + ([&Addr])
  --+|[br]
  --+1st D20:|[$Res1]&nbsp;&nbsp;&nbsp;[b]2nd D20:[/b][$Res2]

  --~Res|math;min;[$Res1];[$Res2]

  --+|[hr]

  --+Result|[$Res.Total]
  --#title|[$Res.Raw] ([$Res1.Raw],[$Res2.Raw])

  --+|[r][rbutton]Again::ROLLDISADV[/rbutton]&nbsp;&nbsp;[rbutton]Roll Dice::TOP[/rbutton][/r]
  --x|

}}