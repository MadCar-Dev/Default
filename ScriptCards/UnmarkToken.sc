!script {{  

  --/|Script Name : Unmark/Clear Token Status Marker
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, TokenMod
  --/|Author      : Will M.

  --/|Description : Gives user an option to remove or clear status markers
  --/|              

  --#reentrant|UnMarkToken
  --#title|Unmark Token
  --#titleCardBackground|#932729
  --#whisper|self
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#debug|1
  --#hidecard|0

  --&TokenId|@{selected|token_id}
  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
  --&trStyle1|style="border:0px dashed black;"
  --&tdStyle1|style="width:50%;text-align:left;background-color:#FFFFFF;font-size:100%"
  --&tdStyle2|style="width:50%;text-align:left;background-color:#FFFFFF;font-size:100%"

  --~|array;statusmarkers;aryToken_SM;@{selected|token_id}

  --/|Loop through all of the status markers on the selected token 
  --~SMItem|array;getfirst;aryToken_SM

    --?[&SMItem] -eq ArrayError|ENDLOOP
    --:LOOPCHECK|

      --&SMItem_Name|[&SMItem]

      --/| The rbutton syntax doesn't like '::' in the name- messes with the parser
      --~SMItem|string;replace;::;^^;[&SMItem]

      --~SMItem_Name|string;before;::;[&SMItem_Name]

      --+|[c][rbutton][sm width=25px][&SMItem_Name][/sm]&nbsp;[&SMItem_Name]::UNMARK;[&TokenId]\[&SMItem][/rbutton][/c]

      --~SMItem|array;getnext;aryToken_SM
    --?[&SMItem] -ne ArrayError|LOOPCHECK
    --:ENDLOOP|

    --+|[br]
    --+|[c][button]Clear All Marks::~Mule|Markers-Clear[/button][/c]

  --X|

  --:UNMARK|
    --#hidecard|1
    --/+Debug-RV|[&reentryval]
    --~Arg|string;split;\;[&reentryval]
    --/+Debug-Args|[&Arg1] / [&Arg2]

    --&TokenId|[&Arg1]
    --&SMItem|[&Arg2]
    --~SMItem|string;replace;^^;::;[&SMItem]
    --&SMItem|-[&SMItem]
    --/+Debug-SM|[&SMItem]

    --@token-mod|_set statusmarkers|[&SMItem] _ids [&TokenId] _ignore-selected

  --X|
}}