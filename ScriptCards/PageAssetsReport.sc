!script{{

  --/|Script Name : Page Assets Report
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, PingToken*
  --/|Author      : Will M.

  --/|Description : Lists all assets within a userselected range of the current selected token
  --/|              Groups based on Asset Type (Drawing, Character, NPC, GMToken, Other)

  --/|Note : Although not required, the PingToken utility is very handy for this report.  It
  --/|       will ping on the map when the user clicks on the asset name.  PingToken can be 
  --/|       found in my API Scripts folder

  --/|neat trick, you can default rentry vals by assigning them before you ever make a call
  --&reentryval|100
  --:TOP|
  --#reentrant|PageAssets
  
  --#title|Page Assets Report
  --#titleCardBackground|#03038a
  --#titlefontface|Patrick Hand
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|10px
  --#sourceToken|@{selected|token_id}
  --#hidecard|0    
  --#whisper|self
  --#debug|0
  --/|#activepage|[*C:playerpageid]
  --#activepage|[*S:t-_pageid]
  --#leftsub|[*P:name]

  --?X[*P:snapping_increment] -eq X|&SI;1|&SI;[*P:snapping_increment]

  --#rightsub|[*P:width] x [*P:height] ([&SI]/[*P:scale_number])

  --Ssettings|Page Assets Report

  --=Rng|[&reentryval]

  --&tbl|

  --&tblCharacters|
  --&tblNPCs|
  --&tblMapItems|
  --&tblGMTokens|
  --&tblOther|

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
  --&trStyle1|style="border-top:1px dashed black;"
  --&trStyle2|style="border:0px dashed black;"
  --&tdStyle1|style="width:30%;text-align:left;background-color:#FFFFFF;font-size:80%"
  --&tdStyle2|style="width:30%;text-align:center;background-color:#FFFFFF;font-size:80%"
  --&tdStyle3|style="width:40%;text-align:right;background-color:#FFFFFF;font-size:80%"

  --~TokenCnt|array;pagetokens;alltokens;@{selected|token_id}

  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens

    --?[&TokenId] -eq ArrayError|ENDLOOP
    --:LOOPCHECK|

      --/|Skip this token if it is out of the defined range 
      -->IS_TOKEN_IN_RANGE|@{selected|token_id};[&TokenId];[$Rng] --?[&IN_RANGE] -eq 0|CONTINUE

      --&CharId|[*[&TokenId]:t-represents]

      --&tname|[*[&TokenId]:t-name]
      --~len|string;length;[&tname]
      --/+debug|[img width=25 height=25][*[&TokenId]:t-imgsrc][/img] [&tname] ([$len]) [*[&TokenId]:t-layer] [*[&TokenId]:t-represents]

      --?[$len.Total] -gt 0|SKIP1 --&tname|Blank --:SKIP1|

      --&IsDrawingType|Drawing
      --?[*[&TokenId]:t-isdrawing] -eq true|SKIP2 --&IsDrawingType|Token --:SKIP2|

      --/|Round Width and Height to reduce number of potential numbers after decimal point
      --=Width|[*[&TokenId]:t-width] \ 1 
      --=Height|[*[&TokenId]:t-height] \ 1 

      --&Row|[tr [&trStyle1]] 
            [td [&tdStyle1]] [img width=25 height=25][*[&TokenId]:t-imgsrc][/img] [/td]
            [td [&tdStyle2]] [rbutton][&tname]::FIND_TOKEN;[&TokenId][/rbutton] [/td] 
            [td [&tdStyle3]] [*[&TokenId]:t-layer] [/td] [/tr]
          [tr [&trStyle2]] 
            [td [&tdStyle1]] [$Width.Total]   x   [$Height.Total] ([$DIST.Total]) [/td]
            [td [&tdStyle2]] [&IsDrawingType] [/td] 
            [td [&tdStyle3]] [&TokenId] [/td] [/tr]

      --/| Test to see what kind of token we are looking at
      --/?"[*[&CharId]:PlayerCharacter]" -eq "1"|ISCHARACTER
      --?"[*[&TokenId]:t-represents]" -inc "-" -and "[*[&TokenId]:NPC]" -ne "1"|ISCHARACTER
      --?"[*[&TokenId]:NPC]" -eq "1"|ISNPC
      --?"[*[&TokenId]:t-layer]" -eq "gmlayer"|ISGMTOKEN      
      --?"[*[&TokenId]:t-layer]" -eq "map"|ISMAPTOKEN     
      -->ISOTHER|

      --/: Types of Tokens to Group and report on (Character, NPC, Map Item, ...)
      --/: Track for all tokens (Name, TokenId, Layer, _subtype, imgsrc, represents, isdrawing, )
      --:ISCHARACTER|
        --&tblCharacters|+ [&Row]

      -->CONTINUE|
      --:ISNPC|
        --&tblNPCs|+ [&Row]

      -->CONTINUE|
      --:ISGMTOKEN|
        --&tblGMTokens|+ [&Row]
 
      -->CONTINUE|
      --:ISMAPTOKEN|
        --&tblMapItems|+ [&Row]

      -->CONTINUE|
      --:ISOTHER|
        --&tblOther|+ [&Row]

      -->CONTINUE|

    --:CONTINUE|

    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|LOOPCHECK
    --:ENDLOOP|

    -->SECTION_HEADER|Range From Selected Token ([$Rng.Total] ft.)
    --/| Ranges: 10, 20, 50, 100, 250, 500, All
    --+|[c][rbutton]10::TOP;10[/rbutton]&nbsp;
           [rbutton]20::TOP;20[/rbutton]&nbsp;
           [rbutton]50::TOP;50[/rbutton]&nbsp;
           [rbutton]100::TOP;100[/rbutton]&nbsp;
           [rbutton]250::TOP;250[/rbutton]&nbsp;
           [rbutton]500::TOP;500[/rbutton]&nbsp; 
           [rbutton]All::TOP;1000[/rbutton][/c]

    -->SECTION_HEADER|Legend
    --&tblLegend|[tr [&trStyle1]]
            [td [&tdStyle1]][b] Image [/b][/td]
            [td [&tdStyle2]][b] Name(Click to find) [/b][/td] 
            [td [&tdStyle3]][b] Layer [/td] [/b][/tr]
          [tr [&trStyle2]] 
            [td [&tdStyle1]][b] Size (Range)[/b][/td]
            [td [&tdStyle2]][b] Token/Drawing [/b][/td] 
            [td [&tdStyle3]][b] Token-Id [/td] [/b][/tr]      
      --+| [t [&tStyle]] [&tblLegend] [/t]

    -->SECTION_HEADER|Characters

      --+| [t [&tStyle]] [&tblCharacters] [/t]
    
    -->SECTION_HEADER|NPCs
      --+|[t [&tStyle]] [&tblNPCs][/t]    

    -->SECTION_HEADER|GM Tokens
      --+|[t [&tStyle]] [&tblGMTokens][/t]    

    -->SECTION_HEADER|Map Items
      --+|[t [&tStyle]] [&tblMapItems][/t]    

    -->SECTION_HEADER|Other

      --+|[t [&tStyle]] [&tblOther][/t]

  --X|
--<|

--:FIND_TOKEN|TokenId
  --#hidecard|1  
  --@ping-token|_[&reentryval] _[&SendingPlayerID]
  --X|
--<|

--:SECTION_HEADER|Title
  --&hdrT|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 1px black;border: 0px solid black;"
  --&hdrTR|style="border:0px solid black;"
  --&hdrTD|style="width:100%;background-color:#d8d8e6;font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrT]] [tr [&hdrTR]] [td [&hdrTD]] [c] [%1%] [/c] [/td] [/tr] [/t]
--<|

--:IS_TOKEN_IN_RANGE|Anchor Token, Checked Token, Range (in units)
  --&AnchorToken|[%1%]
  --&TestToken|[%2%]
  --=Range|[%3%]

  --&IN_RANGE|0
  --/:CHECK DISTANCE IN ft.|
  --~d|euclideandistance;[&AnchorToken];[&TestToken]
  --=SnapInc|[&SI] --~SnapInc|math;max;.1;[$SnapInc]
  --=Scale|[*P:scale_number] --~Scale|math;max;1;[$Scale]
  --=DIST|[$d] / [$SnapInc] * [$Scale] --~DIST|math;round;[$DIST]
  --?[$DIST] -gt [$Range]|&IN_RANGE;0|&IN_RANGE;1
  --<|Return 

}}          