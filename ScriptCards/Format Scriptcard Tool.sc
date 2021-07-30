!script {{  
  --/| Set gDebug to 2 or more to dump out internal debugging text
  --&gDEBUG|2

  --/|This is a utility script for Scriptcard Writers to quickly
  --/|test out various color and font configurations.  You can 
  --/|save a desired settings so that it can loaded in other 
  --/|scriptcards with the Lsettings scriptcard command, or you 
  --/|can Dump out the current settings and copy them into a text editor
  --/| 
  --/|SC Format Utility 0.8 - Updated 6.24.2021
  --/| * Improved overall look and feel 
  --/| * Adding new 1.3.7 background seetings for buttons and rows
  --/|    * bodybackgroundimage (url for image or CSS Gradient formula)
  --/|    * evenrowbackgroundimage (url for image or CSS Gradient formula)
  --/|    * oddrowbackgroundimage (url for image or CSS Gradient formula)
  --/|    * buttonbackgroundimage (url for image or CSS Gradient formula)
 
  --/|Updates for version 0.7 - Updated 6.21.2021
  --/|  Compatible with version ScriptCards 1.3.6+

  --/|Previous Updates
  --/| * Now Utlizes scriptcards 1.3.6 functionality 
  --/| * Toggle fields that are only one or two values (usehollowdice, emotestate)
  --/| * dump settings in Scriptcard format (--#TitleFontFace|...)
  --/| * Improve look of the main Card
  --/| * Add Date/Time and Timezone Settings
  --/| * Add other misc settings (debug, Hidetitlecard, SubtitleSeparator)
  --/| * Add a sample card 
  --/| * Added new settings found in 1.3.6 (titlecardbackgroundimage, titlecardgradient, norollhighlight)
  --/| * Linear, Conic and Radial Gradient CSS builder for TitleBackground added
  --/| * Add ability to Enter Hex Color on Gradient Selector

  --/|Future Updates
  --/| * Add a collection of eye-pleaseing schemas 
  --/| * Add a gradients cheat sheet for selecting fun gradient titlecard settings (Circular and Conic)

  --#reentrant|SCFormatUtil
  --#emoteText|This is a handy dandy SC Format Utility.  Let me know how this works for you and how it could be improved.

  -->LOG|2;TOP;At top of main routine

  --:TOP|
  --#title|SC Format Utility 0.9
  --Ssettings|SCFU

  -->LOAD_CURRENT_SETTINGS|

  --#leftSub|[&titlecardbackground]
  --#rightSub|[&titlefontface] ([&titlefontsize])

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
  --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground];color:[&oddrowfontcolor];"
  --&tdStyle1|style="width:20%;text-align:right;font-size:100%;padding:2px;"
  --&tdStyle2|style="width:30%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle3|colspan=3 style="width:80%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle4|colspan=2 style="width:30%;text-align:right;font-size:90%;padding:2px;"
  --&tdStyle5|colspan=2 style="width:70%;text-align:left;font-size:90%;padding:2px;"

  -->SECTION_HEADER|[rbutton]Title Settings::TITLE_SETTINGS[/rbutton]

  --/| 4 column table 
  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle1]][b]Image/Gradient:[/b][/td] [td [&tdStyle3]][&titlecardbackgroundimage][/td]
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][b]Color:[/b][/td] [td [&tdStyle2]][&titlecardbackground][/td]
            [td [&tdStyle1]][b]Gradient:[/b][/td] [td [&tdStyle2]][&titlecardgradient][/td]
        [/tr][tr [&trStyle1]]          
          [td [&tdStyle1]][b]Font:[/b][/td] [td [&tdStyle2]][&titlefontface] [&titlefontsize][/td]
          [td [&tdStyle1]][b]Line Height:[/b][/td] [td [&tdStyle2]][&titlefontlineheight][/td]
        [/tr][tr [&trStyle2]]
          [td [&tdStyle1]][b]Hide Card?[/b][/td] [td [&tdStyle2]][&hidetitlecard][/td]
          [td [&tdStyle1]][b]Separator:[/b][/td] [td [&tdStyle2]][&subtitleseparator][/td]
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][b]Table Radious:[/b][/td] [td [&tdStyle2]][&tableborderradius][/td]
            [td [&tdStyle1]][b]Shaddow:[/b][/td] [td [&tdStyle2]][&tableshadow][/td]
        [/tr]
      [/t]

  -->SECTION_HEADER|[rbutton]Body and Row Settings::BODY_ROW_SETTINGS[/rbutton]

  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle1]][b]Font:[/b][/td] [td [&tdStyle2]][&bodyfontface] [&bodyfontsize][/td]
            [td [&tdStyle1]][b]Roll Higlight:[/b][/td] [td [&tdStyle2]][&norollhighlight][/td]
        [/tr][tr [&trStyle1]]
          [td [&tdStyle4]][b]Body BG Image:[/b][/td] [td [&tdStyle5]][&bodybackgroundimage][/td]
        [/tr][tr [&trStyle2]]
          [td [&tdStyle4]][b]Odd Row Font / Back Color:[/b][/td] [td [&tdStyle5]][&oddrowfontcolor] / [&oddrowbackground][/td]
        [/tr][tr [&trStyle1]]
          [td [&tdStyle4]][b]Odd Row BG-Image:[/b][/td] [td [&tdStyle5]][&oddrowbackgroundimage][/td]
        [/tr][tr [&trStyle2]]
          [td [&tdStyle4]][b]Even Row Font / Back Color:[/b][/td] [td [&tdStyle5]][&evenrowfontcolor] / [&evenrowbackground][/td]
        [/tr][tr [&trStyle1]]
          [td [&tdStyle4]][b]Even Row BG-Image:[/b][/td] [td [&tdStyle5]][&evenrowbackgroundimage][/td]
        [/tr]
      [/t]

  -->SECTION_HEADER|[rbutton]Button Settings::BUTTON_SETTINGS[/rbutton]
  --+|[t [&tStyle]]
        [tr [&trStyle1]]
          [td [&tdStyle1]][b]Text Color:[/b][/td] [td [&tdStyle2]][&buttontextcolor][/td]
          [td [&tdStyle1]][b]Font:[/b][/td] [td [&tdStyle2]][&buttonfontface] [&buttonfontsize][/td]
        [/tr][tr [&trStyle2]]          
          [td [&tdStyle1]][b]Background:[/b][/td] [td [&tdStyle2]][&buttonbackground][/td]
          [td [&tdStyle1]][b]Border:[/b][/td] [td [&tdStyle2]][&buttonbordercolor][/td]
        [/tr][tr [&trStyle1]]
          [td [&tdStyle1]][b]Background Image:[/b][/td] [td [&tdStyle3]][&buttonbackgroundimage][/td]
        [/tr][tr [&trStyle1]]
          [td colspan=4][c][rbutton]Example::EXAMPLE_CARD[/rbutton][/c][/td]
        [/tr]
      [/t]

  -->SECTION_HEADER|[rbutton]Dice Settings::DICE_SETTINGS[/rbutton]
  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle1]][b]Color:[/b][/td] [td [&tdStyle2]][&dicefontcolor][/td]
            [td [&tdStyle1]][b]Size:[/b][/td] [td [&tdStyle2]][&dicefontsize][/td]
        [/tr][tr [&trStyle2]]          
          [td [&tdStyle1]][b]Hollow?[/b][/td] [td [&tdStyle2]][&usehollowdice][/td]
          [td [&tdStyle1]]&nbsp;[/td] [td [&tdStyle2]]&nbsp;[/td]          
        [/tr]
      [/t]

    --+|[c][d4]4[/d4][d6]6[/d6][d8]8[/d8][d10]10[/d10][d12]12[/d12][d20]20[/d20][/c]

  -->SECTION_HEADER|[rbutton]Emote & Misc Settings::EMOTE_SETTINGS[/rbutton]  

  --~datetime|system;date;getdatetime
  --+|[t [&tStyle]]
      [tr [&trStyle1]]
          [td [&tdStyle1]][b]Emote Color:[/b][/td] [td [&tdStyle2]][&emotebackground][/td]
          [td [&tdStyle1]][b]State:[/b][/td] [td [&tdStyle2]][&emotestate][/td]
      [/tr] [tr [&trStyle2]]          
          [td [&tdStyle1]][b]Timezone:[/b][/td] [td [&tdStyle2]][&timezone][/td]
          [td [&tdStyle1]][b]Debug?[/b][/td] [td [&tdStyle2]][&debug][/td]
      [/tr][tr [&trStyle1]]          
          [td [&tdStyle1]][b]Date/Time:[/b][/td] [td [&tdStyle3]][&datetime][/td]
        [/tr]
      [/t]

  -->SECTION_HEADER|Settings Management
  --+|[c][rbutton]Save::SAVE_CURRENT_SCHEMA[/rbutton]
      [rbutton]Save as Default::SAVE_CURRENT_SCHEMA;Default[/rbutton]
      [rbutton]Load::LOAD_SAVED_SCHEMA[/rbutton]
      [rbutton]Load Default::LOAD_SAVED_SCHEMA;Default[/rbutton]
      [rbutton]List::SHOW_SAVED_SCHEMAS[/rbutton] 
      [rbutton]Dump::DUMP_CURRENT_SETTINGS[/rbutton] 
      [rbutton]&#x1F504;Reset::RESET[/rbutton][/c]

  -->SECTION_HEADER|Helpful Links
    -->WEB_LINK|Hex Colors;https://htmlcolorcodes.com/color-picker/ --&HexColorLink|[&LINK]
    -->WEB_LINK|Named Colors;https://htmlcolorcodes.com/color-names/ --&NamedColorLink|[&LINK]
    -->WEB_LINK|Web Fonts;https://www.w3schools.com/cssref/css_websafe_fonts.asp --&FontLink|[&LINK]
    -->WEB_LINK|EMOJI-Finder;https://emojifinder.com/ --&EmojiLink|[&LINK]
    -->WEB_LINK|Gradients;https://cssgradient.io/ --&GradientLink|[&LINK]
    --+|[c][&HexColorLink]&nbsp;[&NamedColorLink]&nbsp;[&FontLink]&nbsp;[&EmojiLink]&nbsp;[&GradientLink][/c]

  --X|

--/| =======================================================================
--:TITLE_SETTINGS|  
  --#title|Title Settings
  --#leftSub|
  --#rightSub|
  -->LOG|2;TITLE_SETTINGS;

  -->LOAD_CURRENT_SETTINGS|

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
  --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"
  --&tdStyle1|style="width:60%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle2|style="width:40%;text-align:Right;font-size:100%;padding:2px;"
  --&tdStyle3|colspan=2 style="width:100%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle4|colspan=2 style="width:100%;text-align:center;font-size:100%;padding:2px;border:1px solid black;"

  --/| 2 column table 

  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle4]] [&titlecardbackgroundimage] [/td]
        [/tr][tr [&trStyle2]]
            [td [&tdStyle3]][rbutton]Edit::SELECT_GRADIENT_TYPE;titlecardbackgroundimage\TITLE_SETTINGS[/rbutton] Title Card Background Image or Gradient[/td]
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;TitleCardBackground_ByName\TITLE_SETTINGS[/rbutton] Background Color[/td]
            [td [&tdStyle2]][&titlecardbackground][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Toggle::TOGGLE_PARAM;titlecardgradient\0\1\TITLE_SETTINGS[/rbutton] Simple Graident? (0-Off/1-On)[/td]
            [td [&tdStyle2]][&titlecardgradient][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;titleFontFace\TITLE_SETTINGS[/rbutton] Font Face[/td]
            [td [&tdStyle2]][&titlefontface][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;titleFontSize\TITLE_SETTINGS[/rbutton] Font Size[/td]
            [td [&tdStyle2]][&titlefontsize][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;titleFontLineHeight\TITLE_SETTINGS[/rbutton] Line/Title Height[/td]
            [td [&tdStyle2]][&titlefontlineheight][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;SubtitleSeparator\TITLE_SETTINGS[/rbutton] Subtitle Separator[/td]
            [td [&tdStyle2]][&subtitleseparator][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Toggle::TOGGLE_PARAM;hideTitleCard\0\1\TITLE_SETTINGS[/rbutton] Hide Card (0-Show/1-Hide)[/td]
            [td [&tdStyle2]][&hidetitlecard][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;tableBorderRadius\TITLE_SETTINGS[/rbutton] Table Border Radius[/td]
            [td [&tdStyle2]][&tableborderradius][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;tableshadow\TITLE_SETTINGS[/rbutton] Table Shadow[/td]
            [td [&tdStyle2]][&tableshadow][/td]            
        [/tr]
      [/t]

    --+|[hr][c][rbutton]Return::TOP[/rbutton][/c]

  --X|

--/| =======================================================================
--:BODY_ROW_SETTINGS|  
  --#title|Body/Row Settings
  --#leftSub|
  --#rightSub|
  -->LOG|2;BODY_ROW_SETTINGS;

  -->LOAD_CURRENT_SETTINGS|

  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black; white-space: normal; white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
  --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"
  --&tdStyle1|style="width:40%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle2|style="width:60%;text-align:Right;font-size:100%;padding:2px;"
  --&tdStyle3|style="width:100%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle4|style="width:100%;text-align:center;font-size:100%;padding:2px;border:1px solid black;"

  --/| 2 column table 

  --/|+&#45;&#45;&#35;evenrowbackgroundimage&#124;|[&evenrowbackgroundimage]
  --/|+&#45;&#45;&#35;oddrowbackgroundimage&#124;|[&oddrowbackgroundimage]

  --+|[c][b]Body Settings[/b][/c]
  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;bodyFontFace\BODY_ROW_SETTINGS[/rbutton] Font Face[/td]
            [td [&tdStyle2]][&bodyfontface][/td]
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;bodyFontSize\BODY_ROW_SETTINGS[/rbutton] Font Size[/td]
            [td [&tdStyle2]][&bodyfontsize][/td]
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Toggle::TOGGLE_PARAM;norollhighlight\0\1\BODY_ROW_SETTINGS[/rbutton] Roll Highlight (0-Show/1-Hide)[/td]
            [td [&tdStyle2]][&norollhighlight][/td]            
        [/tr][tr [&trStyle2]]            
            [td [&tdStyle1]][rbutton]Edit::SELECT_GRADIENT_TYPE;bodybackgroundimage\BODY_ROW_SETTINGS[/rbutton] Background Image/Gradient[/td]
            [td [&tdStyle2]][&bodybackgroundimage][/td]            
        [/tr]
      [/t]

  --+|[br]            
  --+|[c][b]Row Settings[/b][/c]

  --+|[t [&tStyle]]
        [tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;oddRowFontColor_ByName\BODY_ROW_SETTINGS[/rbutton] Odd Row Font Color[/td]
            [td [&tdStyle2]][&oddrowfontcolor][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;oddRowBackground_ByName\BODY_ROW_SETTINGS[/rbutton] Odd Row Back Color[/td]
            [td [&tdStyle2]][&oddrowbackground][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::SELECT_GRADIENT_TYPE;oddrowbackgroundimage\BODY_ROW_SETTINGS[/rbutton] Odd Row Back Image/Gradient[/td]
            [td [&tdStyle2]][&oddrowbackgroundimage][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;evenRowFontColor_ByName\BODY_ROW_SETTINGS[/rbutton] Even Row Font Color[/td]
            [td [&tdStyle2]][&evenrowfontcolor][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;evenRowBackground_ByName\BODY_ROW_SETTINGS[/rbutton] Even Row Back Color[/td]
            [td [&tdStyle2]][&evenrowbackground][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::SELECT_GRADIENT_TYPE;evenrowbackgroundimage\BODY_ROW_SETTINGS[/rbutton] Even Row Back Image/Gradient[/td]
            [td [&tdStyle2]][&evenrowbackgroundimage][/td]            
        [/tr]
      [/t]
    --+|[hr][c][rbutton]Return::TOP[/rbutton][/c]
  --X|

--/| =======================================================================
--:BUTTON_SETTINGS|  
  --#title|Button Settings
  --#leftSub|
  --#rightSub|
  -->LOG|2;BUTTON_SETTINGS;

  -->LOAD_CURRENT_SETTINGS|

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
  --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"
  --&tdStyle1|style="width:60%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle2|style="width:40%;text-align:Right;font-size:100%;padding:2px;"
  --&tdStyle3|style="width:100%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle4|style="width:100%;text-align:center;font-size:100%;padding:2px;border:1px solid black;"
  --/| 2 column table 

  --+&#45;&#45;&#35;buttonbackgroundimage&#124;|[&buttonbackgroundimage]

  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;buttonFontFace\BUTTON_SETTINGS[/rbutton] Font Face[/td]
            [td [&tdStyle2]][&buttonfontface][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;buttonFontSize\BUTTON_SETTINGS[/rbutton] Font Size[/td]
            [td [&tdStyle2]][&buttonfontsize][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;buttonTextColor_ByName\BUTTON_SETTINGS[/rbutton] Text Color[/td]
            [td [&tdStyle2]][&buttontextcolor][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;buttonBackGround_ByName\BUTTON_SETTINGS[/rbutton] Background Color[/td]
            [td [&tdStyle2]][&buttonbackground][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;buttonBorderColor_ByName\BUTTON_SETTINGS[/rbutton] Border Color[/td]
            [td [&tdStyle2]][&buttonbordercolor][/td]            
        [/tr][tr [&trStyle1]]            
            [td [&tdStyle1]][rbutton]Edit::SELECT_GRADIENT_TYPE;buttonbackgroundimage\BUTTON_SETTINGS[/rbutton] Background Image/Gradient[/td]
            [td [&tdStyle2]][&buttonbackgroundimage][/td]            
        [/tr]
      [/t]
    --+|[hr][c][rbutton]Return::TOP[/rbutton][/c]
  --X|

--/| =======================================================================
--:DICE_SETTINGS|
  --#title|Dice Settings
  --#leftSub|
  --#rightSub|

  -->LOG|2;DICE_SETTINGS;

  -->LOAD_CURRENT_SETTINGS|

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
  --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"
  --&tdStyle1|style="width:60%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle2|style="width:40%;text-align:Right;font-size:100%;padding:2px;"
  --&tdStyle3|style="width:100%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle4|style="width:100%;text-align:center;font-size:100%;padding:2px;border:1px solid black;"

  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;diceFontColor_ByName\DICE_SETTINGS[/rbutton] Color[/td]
            [td [&tdStyle2]][&dicefontcolor][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;dicefontsize\DICE_SETTINGS[/rbutton] Size[/td]
            [td [&tdStyle2]][&dicefontsize][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Toggle::TOGGLE_PARAM;usehollowdice\0\1\DICE_SETTINGS[/rbutton] Hollow (0-Solid/1-Hollow)?[/td]
            [td [&tdStyle2]][&usehollowdice][/td]            
        [/tr]
      [/t]

    --+|[c][d4]4[/d4][d6]6[/d6][d8]8[/d8][d10]10[/d10][d12]12[/d12][d20]20[/d20][/c]

    --+|[hr][c][rbutton]Return::TOP[/rbutton][/c]
  --X|

--/| =======================================================================
--:EMOTE_SETTINGS|
  --#title|Emote and Misc Settings
  --#leftSub|
  --#rightSub|

  -->LOG|2;EMOTE_SETTINGS;

  -->LOAD_CURRENT_SETTINGS|

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
  --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"
  --&tdStyle1|style="width:60%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle2|style="width:40%;text-align:Right;font-size:100%;padding:2px;"
  --&tdStyle3|style="width:100%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle4|style="width:100%;text-align:center;font-size:100%;padding:2px;border:1px solid black;"


  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Edit::CHANGE_PARAM;emotebackground_ByName[/rbutton] Emote Color[/td]
            [td [&tdStyle2]][&emotebackground][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Toggle::TOGGLE_PARAM;emoteState\visible\off\EMOTE_SETTINGS[/rbutton] Emote State (0-Show/1-Hide)?[/td]
            [td [&tdStyle2]][&emotestate][/td]            
        [/tr][tr [&trStyle1]]
            [td [&tdStyle1]][rbutton]Toggle::TOGGLE_PARAM;Debug\0\1\EMOTE_SETTINGS[/rbutton] Debug (0-Off/1-On)?[/td]
            [td [&tdStyle2]][&debug][/td]            
        [/tr][tr [&trStyle2]]
            [td [&tdStyle1]][rbutton]Toggle::SELECT_TIME_ZONE\EMOTE_SETTINGS[/rbutton] TimeZone[/td]
            [td [&tdStyle2]][&timezone][/td]            
        [/tr]
      [/t]
    --~datetime|system;date;getdatetime
    --+Date/Time|[&datetime]

    --+|[hr][c][rbutton]Return::TOP[/rbutton][/c]
  --X|

--/| =======================================================================
--:TOGGLE_PARAM|PARAM;val1;val2;return_label
  --~Arg|string;split;\;[&reentryval]
  --&PARAM|[&Arg1]
  --&Val1|[&Arg2]
  --&Val2|[&Arg3]  
  --&RETURN_LABEL|[&Arg4]  
  -->LOG|2;TOGGLE_PARAM;[&reentryval]

  --~PValue|system;readsetting;[&PARAM]

  --/|*TOGGLE|[&PARAM]([&Val1]/[&Val2])[&PValue]

  --?[&PValue] -eq [&Val1]|&PValue;[&Val2]|&PValue;[&Val1]
  --#[&PARAM]|[&PValue]
  --+[&PARAM] set to:|[&PValue]  

  --?X[&RETURN_LABEL] -eq X|^TOP
  --^[&RETURN_LABEL]|
  --<|

--/| =======================================================================
--:CHANGE_PARAM|PARAM
  --~Arg|string;split;\;[&reentryval]
  --&PARAM|[&Arg1]
  --&RETURN_LABEL|[&Arg2]  
  --~PARAM|string;tolowercase;[&PARAM]  
  -->LOG|2;CHANGE_PARAM;[&reentryval]

  --?[&PARAM] -eq titlecardbackground_byname|TCBG_N
  --?[&PARAM] -eq titlecardbackground_byhex|TCBG_H
  --?[&PARAM] -eq titlecardbackgroundimage|GET_URL
  --?[&PARAM] -eq titlecardbackgroundimage_raw|GET_URL_RAW
  --?[&PARAM] -eq titlefontsize|TFS
  --?[&PARAM] -eq titlefontlineheight|TFLH
  --?[&PARAM] -eq titlefontface|TFF
  --?[&PARAM] -eq subtitleseparator|TSS

  --?[&PARAM] -eq bodyfontsize|BFS
  --?[&PARAM] -eq bodyfontface|BFF
  --?[&PARAM] -eq bodybackgroundimage|GET_URL
  --?[&PARAM] -eq bodybackgroundimage_raw|GET_URL_RAW  

  --?[&PARAM] -eq oddrowbackground_byname|ORBG_N
  --?[&PARAM] -eq oddrowfontcolor_byname|ORFC_N
  --?[&PARAM] -eq evenrowbackground_byname|ERBG_N
  --?[&PARAM] -eq evenrowfontcolor_byname|ERFC_N
  --?[&PARAM] -eq oddrowbackground_byhex|ORBG_H
  --?[&PARAM] -eq oddrowfontcolor_byhex|ORFC_H
  --?[&PARAM] -eq evenrowbackground_byhex|ERBG_H
  --?[&PARAM] -eq evenrowfontcolor_byhex|ERFC_H
  --?[&PARAM] -eq oddrowbackgroundimage|GET_URL
  --?[&PARAM] -eq oddrowbackgroundimage_raw|GET_URL_RAW
  --?[&PARAM] -eq evenrowbackgroundimage|GET_URL
  --?[&PARAM] -eq evenrowbackgroundimage_raw|GET_URL_RAW

  --?[&PARAM] -eq buttonbackground_byname|BTNBG_N
  --?[&PARAM] -eq buttontextcolor_byname|BTNTC_N
  --?[&PARAM] -eq buttonbordercolor_byname|BTNBC_N
  --?[&PARAM] -eq buttonbackground_byhex|BTNBG_H
  --?[&PARAM] -eq buttontextcolor_byhex|BTNTC_H
  --?[&PARAM] -eq buttonbordercolor_byhex|BTNBC_H
  --?[&PARAM] -eq buttonfontsize|BTNFS
  --?[&PARAM] -eq buttonfontface|BTNFF
  --?[&PARAM] -eq buttonbackgroundimage|GET_URL
  --?[&PARAM] -eq buttonbackgroundimage_raw|GET_URL_RAW

  --?[&PARAM] -eq dicefontcolor_byname|DFC_N
  --?[&PARAM] -eq dicefontcolor_byhex|DFC_H
  --?[&PARAM] -eq dicefontsize|DFS
  --?[&PARAM] -eq usehollowdice|UHD

  --?[&PARAM] -eq emotebackground_byname|EBG_N
  --?[&PARAM] -eq emotebackground_byhex|EBG_H  
  --?[&PARAM] -eq emotestate|ES

  --?[&PARAM] -eq tableborderradius|TBR      
  --?[&PARAM] -eq tableshadow|TS

  --+ERROR|Parmeter [&PARAM] not found

  --^TOP|

  --:TCBG_N|
    -->SET_COLOR_BY_NAME|BLANK;titlecardbackground;TITLE_SETTINGS
  --^TITLE_SETTINGS|

  --:TCBG_H|
    --I;Set Title Card Background Color|q;UserInput;Title Card Background Color (fmt: #123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]
    --#TitleCardBackground|#[&UserInput]
    --+TitleCardBackground set to:|[&UserInput]
    --+|[HR]    
  --^TITLE_SETTINGS|

  --:GET_URL|
    --I;Set [&PARAM] URL|q;UserInput;[&PARAM] URL (Fmt: https://.../file.png)?
    --&UserInput|url([&UserInput]));background-size: auto;background-repeat: round;
    --#[&PARAM]|[&UserInput]
    --+[&PARAM] set to:|[&UserInput]
    --+|[HR]    
  --^[&RETURN_LABEL]|

  --:GET_URL_RAW|

    --~PARAM|string;replace;_raw;;[&PARAM]
    --I;Set [&PARAM] (Open Format)|q;UserInput;[&PARAM] URL or Gradient syntax?
    --#[&PARAM]|[&UserInput]
    --+[&PARAM] set to:|[&UserInput]
    --+|[HR]    
  --^[&RETURN_LABEL]|


  --:TFS|
    --I;Set Title Card Font Size|q;UserInput;Title Card Font Size?|12px|13px|15px|16px|18px|20px|24px|28px|30px|35px|40px|0.5em|0.8em|1.0em|1.2em|1.5em|2.0em|2.5em|3.0em|4.0em|5.0em|6.0em|8.0em|10.0em
    --#titlefontsize|[&UserInput]
    --+TitleFontSize set to:|[&UserInput]
    --+|[HR]    
  --^TITLE_SETTINGS|

  --:TFLH|
    --I;Set Title Card Line Height|q;UserInput;Title Card Line Height?|12px|13px|15px|16px|18px|20px|24px|28px|30px|35px|40px|0.5em|0.8em|1.0em|1.2em|1.5em|2.0em|2.5em|3.0em|4.0em|5.0em|6.0em|8.0em|10.0em
    --#titlefontlineheight|[&UserInput]
    --+TitleFontLineHeight set to:|[&UserInput]
    --+|[HR]    
  --^TITLE_SETTINGS|

  --:TFF|
    --I;Set Title Card Font Face|q;UserInput;Title Card Font Face (fmt: Font name)?|Arial|Candal|Contrail One|Patrick Hand|Shadows Into Light|Brush Script MT|Courier New|Garamond|Georgia|Helvetica|Tahoma|Times New Roman|Trebuchet MS|Verdana
    --#titlefontface|[&UserInput]
    --+TitleFontFace set to:|[&UserInput]
    --+|[HR]    
  --^TITLE_SETTINGS|

  --:TSS|
    --I;Set Subtitle Separator Character|q;UserInput;Subtitle Separator Character?|☺|☻|♥|♦|♣|♠|•|○|♂|♀|♪|♫|☼|►|◄|◄►|↕|↔|─|═|⌂|■|▬|#|$|%|&|Φ|Θ|Ω|∞|≈|π|σ|«»
    --#subtitleSeperator|&nbsp;[&UserInput]&nbsp;
    --+SubtitleSeparator set to:|[&UserInput]
    --+|[HR]    
  --^TITLE_SETTINGS|


  --:BFS|
    --I;Set Body Font Size|q;UserInput;Body Font Size?|6px|7px|8px|9px|10px|11px|12px|13px|14px|15px|16px|18px|20px|24px|28px|30px|0.5em|0.8em|1.0em|1.2em|1.5em|2.0em|2.5em|3.0em|4.0em|5.0em|6.0em|8.0em|10.0em
    --#bodyfontsize|[&UserInput]
    --+BodyFontSize set to:|[&UserInput]
    --+|[HR]    
  --^BODY_ROW_SETTINGS|

  --:BFF|
    --I;Set Body Font Face|q;UserInput;Body Font Face?|Arial|Candal|Contrail One|Patrick Hand|Shadows Into Light|Brush Script MT|Courier New|Garamond|Georgia|Helvetica|Tahoma|Times New Roman|Trebuchet MS|Verdana
    --#bodyfontface|[&UserInput]
    --+BodyFontFace set to:|[&UserInput]
    --+|[HR]    
  --^BODY_ROW_SETTINGS|

  --:ORBG_N|
    -->SET_COLOR_BY_NAME|BLANK;oddrowbackground;BODY_ROW_SETTINGS
  --^BODY_ROW_SETTINGS|

  --:ORBG_H|
    --I;Set Odd Row Background Color|q;UserInput;Odd Row Background Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]
    --#oddrowbackground|#[&UserInput]
    --+OddRowBackground set to:|[&UserInput]
    --+|[HR]    
  --^BODY_ROW_SETTINGS|

  --:ORFC_N|
    -->SET_COLOR_BY_NAME|BLANK;oddrowfontcolor;BODY_ROW_SETTINGS
  --^BODY_ROW_SETTINGS|

  --:ORFC_H|
    --I;Set Odd Row Font Color|q;UserInput;Odd Row Font Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]    
    --#oddrowfontcolor|#[&UserInput]
    --+OddRowFontColor set to:|[&UserInput]
    --+|[HR]    
  --^BODY_ROW_SETTINGS|

  --:ERBG_N|
    -->SET_COLOR_BY_NAME|BLANK;evenrowbackground;BODY_ROW_SETTINGS
  --^BODY_ROW_SETTINGS|

  --:ERBG_H|
    --I;Set Even Row Background Color|q;UserInput;Even Row Background Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]
    --#evenrowbackground|#[&UserInput]
    --+EvenRowBackground set to:|[&UserInput]
    --+|[HR]    
  --^BODY_ROW_SETTINGS|

  --:ERFC_N|
    -->SET_COLOR_BY_NAME|BLANK;evenrowfontcolor;BODY_ROW_SETTINGS
  --^BODY_ROW_SETTINGS|

  --:ERFC_H|
    --I;Set Even Row Font Color|q;UserInput;Even Row Font Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]
    --#evenrowfontcolor|#[&UserInput]
    --+EvenRowFontColor set to:|[&UserInput]
    --+|[HR]    
  --^BODY_ROW_SETTINGS|

  --:BTNBG_N|
    -->SET_COLOR_BY_NAME|BLANK;buttonbackground;BUTTON_SETTINGS
  --^BUTTON_SETTINGS|

  --:BTNBG_H|
    --I;Set Button Background Color|q;UserInput;Button Background Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]
    --#buttonbackground|#[&UserInput]
    --+ButtonBackground set to:|[&UserInput]
    --+|[HR]    
  --^BUTTON_SETTINGS|

  --:BTNTC_N|
    -->SET_COLOR_BY_NAME|BLANK;buttontextcolor;BUTTON_SETTINGS
  --^BUTTON_SETTINGS|

  --:BTNTC_H|
    --I;Set Button Text Color|q;UserInput;Button Text Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]
    --#buttontextcolor|#[&UserInput]
    --+ButtonTextColor set to:|[&UserInput]
    --+|[HR]    
  --^BUTTON_SETTINGS|

  --:BTNBC_N|
    -->SET_COLOR_BY_NAME|BLANK;buttonbordercolor;BUTTON_SETTINGS
  --^BUTTON_SETTINGS|

  --:BTNBC_H|
    --I;Set Button Border Color|q;UserInput;Button Border Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]    
    --#buttonbordercolor|#[&UserInput]
    --+ButtonBorderColor set to:|[&UserInput]
    --+|[HR]    
  --^BUTTON_SETTINGS|

  --:BTNFS|
    --I;Set Button Font Size|q;UserInput;Button Font Size?|6px|7px|8px|9px|10px|11px|12px|13px|14px|15px|16px|18px|20px|24px|28px|30px|0.5em|0.8em|1.0em|1.2em|1.5em|2.0em|2.5em|3.0em|4.0em|5.0em|6.0em|8.0em|10.0em
    --#buttonfontsize|[&UserInput]
    --+ButtonFontSize set to:|[&UserInput]
    --+|[HR]    
  --^BUTTON_SETTINGS|

  --:BTNFF|
    --I;Set Button Font Face|q;UserInput;Button Font Face (fmt: Font name)?|Arial|Candal|Contrail One|Patrick Hand|Shadows Into Light|Brush Script MT|Courier New|Garamond|Georgia|Helvetica|Tahoma|Times New Roman|Trebuchet MS|Verdana
    --#buttonfontface|[&UserInput]
    --+ButtonFontFace set to:|[&UserInput]
    --+|[HR]    
  --^BUTTON_SETTINGS|

  --:DFC_N|
    -->SET_COLOR_BY_NAME|BLANK;dicefontcolor;DICE_SETTINGS
  --^DICE_SETTINGS|

  --:DFC_H|
    --I;Set Dice Font Color|q;UserInput;Dice Font Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]    
    --#dicefontcolor|#[&UserInput]
    --+DiceFontColor set to:|[&UserInput]
    --+|[HR]    
  --^DICE_SETTINGS|

  --:DFS|
    --I;Set Dice Font Size|q;UserInput;Dice Font Size?|10px|15px|20px|25px|30px|35px|40px|45px|50px|55px|60px|70px|0.5em|0.8em|1.0em|1.2em|1.5em|2.0em|2.5em|3.0em|4.0em|5.0em|6.0em|8.0em|10.0em
    --#dicefontsize|[&UserInput]
    --+DiceFontSize set to:|[&UserInput]
    --+|[HR]    
  --^DICE_SETTINGS|

  --:UHD|
    --I;Use Hollow Dice?|q;UserInput;Use Hollow Dice (Yes-1/No-0)?|1|0
    --#usehollowdice|[&UserInput]
    --+UseHollowDice set to:|[&UserInput]
    --+|[HR]    
  --^DICE_SETTINGS|

  --:EBG_N|
    -->SET_COLOR_BY_NAME|BLANK;emotebackground;EMOTE_SETTINGS
  --^EMOTE_SETTINGS|

  --:EBG_H|
    --I;Set Emote Background Color|q;UserInput;Emote Background Color (#123456)?
    --~UserInput|string;replaceall;#;;[&UserInput]
    --#emotebackground|#[&UserInput]
    --+EmoteBackground set to:|[&UserInput]
    --+|[HR]    
  --^EMOTE_SETTINGS|

  --:ES|
    --I;Set Emote State?|q;UserInput;Set Emote State (Visible/No)?|visible|off
    --#emotestate|[&UserInput]
    --+EmoteState set to:|[&UserInput]
    --+|[HR]    
  --^EMOTE_SETTINGS|

  --:TBR|
    --I;Set Table Border Radius|q;UserInput;Table Border Radius?|0px|1px|2px|3px|4px|5px|6px|7px|8px|10px|12px|15px|18px|20px|24px|30px|0.5em|0.8em|1.0em|1.2em|1.5em|2.0em|2.5em|3.0em|4.0em|5.0em|6.0em|8.0em|10.0em
    --#tableborderradius|[&UserInput]
    --+TableBorderRadius set to:|[&UserInput]
    --+|[HR]    
  --^TITLE_SETTINGS|

  --:TS|
    --I;Set Table Shadow|q;UserInput;Table Shadow (fmt: 5px 3px 3px 0px #aaa; or h-offset v-offset blur spread #Color)?
    --#tableshadow|[&UserInput]
    --+TableShadow set to:|[&UserInput]
    --+|[HR]    
  --^TITLE_SETTINGS|

  --:OTHER|
    --^TOP| Return to the top of the list
  --X|

--:SELECT_GRADIENT_TYPE|
  --~Arg|string;split;\;[&reentryval]
  --&PARAM|[&Arg1]
  --&RETURN_FUNCTION|[&Arg2]
  -->LOG|2;SELECT_GRADIENT_TYPE;[&reentryval]

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
  --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"
  --&tdStyle1|style="width:60%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle2|style="width:40%;text-align:Right;font-size:100%;padding:2px;"
  --&tdStyle3|style="width:100%;text-align:left;font-size:100%;padding:2px;"
  --&tdStyle4|style="width:100%;text-align:center;font-size:100%;padding:2px;border:1px solid black;"

  --/| 2 column table 

  --+|[c][b]Background Image Settings[/b][/c]
  --+|[t [&tStyle]]
        [tr [&trStyle1]]
            [td [&tdStyle4]][b][i][&PARAM][/i][/b][/td]
        [/tr][tr [&trStyle2]]
            [td [&tdStyle3]][rbutton]Edit::CHANGE_PARAM;[&PARAM]_raw\[&RETURN_FUNCTION][/rbutton] Raw Value for background image[/td]
        [/tr][tr [&trStyle1]]
            [td [&tdStyle3]][rbutton]Edit::CHANGE_PARAM;[&PARAM]\[&RETURN_FUNCTION][/rbutton] URL for Image[/td]
        [/tr][tr [&trStyle2]]            
            [td [&tdStyle3]][rbutton]Edit::GRADIENT_LINEAR;1\\\[&PARAM]\[&RETURN_FUNCTION][/rbutton] Linear Gradient[/td]
        [/tr][tr [&trStyle1]]
            [td [&tdStyle3]][rbutton]Edit::GRADIENT_RADIAL;1\\\[&PARAM]\[&RETURN_FUNCTION][/rbutton] Radial Gradient[/td]
        [/tr][tr [&trStyle2]]
            [td [&tdStyle3]][rbutton]Edit::GRADIENT_CONIC;1\\\[&PARAM]\[&RETURN_FUNCTION][/rbutton] Conic Gradient[/td]
        [/tr][/t]
--x|

--/| =======================================================================
--:GRADIENT_RADIAL|Step; Settings
--/| This function will call itself multiple times (circular), building Circle Gradient background syntax as it does
  --~Arg|string;split;\;[&reentryval]
  --&Step|[&Arg1]
  --&Setting|[&Arg2]
  --&AddParam|[&Arg3]
  --&PARAM|[&Arg4]
  --&RETURN_FUNCTION|[&Arg5]
  -->LOG|2;GRADIENT_RADIAL;[&reentryval]

  --&reentryval|

  --/|+GRADIENT_RADIAL RVs|**Step:**[&Step] **Setting:**[&Setting] **New Param:**[&AddParam]

  --C[&Step]|1:GR_STEP1|2:GR_STEP2|3:GR_STEP3|4:GR_STEP4|5:GR_STEP5

  --:GR_STEP1|Step 1: Ask for shape (circular or ellipse)
    --#title|Radial Gradient
    --#leftsub|Step 1
    --#rightsub|Shape 

    --&Setting|radial-gradient(
    --+setting:|[&Setting][hr]
    --+|Select a radial shape for the gradient.  Ellipses usually work best for rectangular fills.  Circles work well on squares.
    --+|[br]
    --+|[c][rbutton]Circular::GRADIENT_RADIAL;2\[&Setting]\circle\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[br][br]
    --+|[c][rbutton]Ellipse (preferred)::GRADIENT_RADIAL;2\[&Setting]\ellipse\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]

  --X|

  --:GR_STEP2|Step 2: Ask for size (none, closest-side, farthest-side, closest-corner, farthest-corner, radius, percentage)
    --#title|Radial Gradient
    --#leftsub|Step 2
    --#rightsub|Size 

    --&Setting|+ [&AddParam]
    --+setting:|[&Setting][hr]

    --+|Select a size for the gradient. Influences the ending shape of the gradient by taking the shape value and instructing where the gradient should end based on the center of the shape.
    --+|[br]
    --+|[c][rbutton]Closest Side::GRADIENT_RADIAL;3\[&Setting]\closest-side\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|The gradient will end at side closest to the center of the shape. 
    --+|[br]
    --+|[c][rbutton]Farthest Side::GRADIENT_RADIAL;3\[&Setting]\farthest-side\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|The opposite of closest-side, where the gradient will end at the side furthest from the shape’s center.
    --+|[br]
    --+|[c][rbutton]Closes Corner::GRADIENT_RADIAL;3\[&Setting]\closest-corner\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|The gradient will end at the corner that matches as the closest to the shape’s center.
    --+|[br]
    --+|[c][rbutton]Farthest Corner::GRADIENT_RADIAL;3\[&Setting]\farthest-corner\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|The opposite of closest-corner, where the gradient ends at the corner that is located furthest from the shape’s center.
    --+|[br]
  --X|

  --:GR_STEP3|Step 3: Ask for position (top, left, right, center, bottom, and combinations top left, top right, center center, top center, center left ...)
    --#title|Radial Gradient
    --#leftsub|Step 3
    --#rightsub|Position 

    --&Setting|+ [&AddParam] at
    --+setting:|[&Setting][hr]

    --+|Select a postion for center of the gradient to start from. 
    --+|[br]
    --+|[c][rbutton]Left Top::GRADIENT_RADIAL;4\[&Setting]\left top\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Center Top::GRADIENT_RADIAL;4\[&Setting]\center top\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Right Top::GRADIENT_RADIAL;4\[&Setting]\right top\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]Left Center::GRADIENT_RADIAL;4\[&Setting]\left center\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Center Center::GRADIENT_RADIAL;4\[&Setting]\center center\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Right Center::GRADIENT_RADIAL;4\[&Setting]\right center\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]Left Bottom::GRADIENT_RADIAL;4\[&Setting]\left bottom\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Center Bottom::GRADIENT_RADIAL;4\[&Setting]\center bottom\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Right Bottom::GRADIENT_RADIAL;4\[&Setting]\right bottom\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[br]
    --+|You can also use percntages(HPos VPos or %Over %Down) to speficy the cente 
    --+|[c][rbutton]15% 25%::GRADIENT_RADIAL;4\[&Setting]\15% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]30% 25%::GRADIENT_RADIAL;4\[&Setting]\30% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]50% 25%::GRADIENT_RADIAL;4\[&Setting]\50% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]70% 25%::GRADIENT_RADIAL;4\[&Setting]\70% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]85% 25%::GRADIENT_RADIAL;4\[&Setting]\75% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]15% 50%::GRADIENT_RADIAL;4\[&Setting]\15% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]30% 50%::GRADIENT_RADIAL;4\[&Setting]\30% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]50% 50%::GRADIENT_RADIAL;4\[&Setting]\50% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]70% 50%::GRADIENT_RADIAL;4\[&Setting]\70% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]85% 50%::GRADIENT_RADIAL;4\[&Setting]\75% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]15% 75%::GRADIENT_RADIAL;4\[&Setting]\15% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]30% 75%::GRADIENT_RADIAL;4\[&Setting]\30% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]50% 75%::GRADIENT_RADIAL;4\[&Setting]\50% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]70% 75%::GRADIENT_RADIAL;4\[&Setting]\70% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]85% 75%::GRADIENT_RADIAL;4\[&Setting]\75% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
  --X|

  --:GR_STEP4|Step 4: Ask for Color List
    --#title|Radial Gradient
    --#leftsub|Step 4
    --#rightsub|List of Colors (2+)

    --&Setting|+ [&AddParam],
    --+setting:|[&Setting][hr]

    --+|[br]
    --+|Select your colors: Pick at least 2, but you can pick more to create a tri-color fade or rainbow effect.
        
    --/|Need to fool it the first time through to think we are calling it from a reentry button
    --&reentryval|BLANK\[&Setting]\RADIAL\[&PARAM]\[&RETURN_FUNCTION]
    -->GET_GRADIENT_COLOR|

  --X|

  --:GR_STEP5|Step 5: Done
    --~Setting|string;trim;[&Setting]
    --~Len|string;length;[&Setting]
    --=Len|[$Len.Text] - 1
    --~Setting|string;left;[$Len];[&Setting]
    --&Setting|+ )

    --/| Need to parameterize setting

    --#[&PARAM]|[&Setting]
    --+[&PARAM] set to:|[&Setting][hr]


    --^[&RETURN_FUNCTION]|

  --+|[hr][c][rbutton]Cancel::[&RETURN_FUNCTION][/rbutton][/c]

--X|

--/| =======================================================================
--:GRADIENT_CONIC|Step; Settings
--/| This function will call itself multiple times (circular), building Circle Gradient background syntax as it does
  --~Arg|string;split;\;[&reentryval]
  --&Step|[&Arg1]
  --&Setting|[&Arg2]
  --&AddParam|[&Arg3]
  --&PARAM|[&Arg4]
  --&RETURN_FUNCTION|[&Arg5]

  -->LOG|2;GRADIENT_CONIC;[&reentryval]
  --&reentryval|

  --C[&Step]|1:GC_STEP1|2:GC_STEP2|3:GC_STEP3
  
  --/| Step 1: Ask for At (default, at HPos% VPos%, at center,  )
  --:GC_STEP1|Step 1: Ask for position (top, left, right, center, bottom, and combinations top left, top right, center center, top center, center left ...)
    --#title|Conic Gradient
    --#leftsub|Step 1
    --#rightsub|Center Position 

    --&Setting|conic-gradient(at 

    --+setting:|[&Setting][hr]

    --+|Select a postion for gradient to be centered on: 
    --+|[br]
    --+|[c][rbutton]Left Top::GRADIENT_CONIC;2\[&Setting]\left top[/rbutton]&nbsp;
           [rbutton]Center Top::GRADIENT_CONIC;2\[&Setting]\center top\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Right Top::GRADIENT_CONIC;2\[&Setting]\right top\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]Left Center::GRADIENT_CONIC;2\[&Setting]\left center\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Center Center::GRADIENT_CONIC;2\[&Setting]\center center\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Right Center::GRADIENT_CONIC;2\[&Setting]\right center\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]Left Bottom::GRADIENT_CONIC;2\[&Setting]\left bottom\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Center Bottom::GRADIENT_CONIC;2\[&Setting]\center bottom\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]Right Bottom::GRADIENT_CONIC;2\[&Setting]\right bottom\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[br]
    --+|You can also use percntages(HPos VPos or %Over %Down) to speficy the cente 
    --+|[c][rbutton]15% 25%::GRADIENT_CONIC;2\[&Setting]\15% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]30% 25%::GRADIENT_CONIC;2\[&Setting]\30% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]50% 25%::GRADIENT_CONIC;2\[&Setting]\50% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]70% 25%::GRADIENT_CONIC;2\[&Setting]\70% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]85% 25%::GRADIENT_CONIC;2\[&Setting]\75% 25%\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]15% 50%::GRADIENT_CONIC;2\[&Setting]\15% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]30% 50%::GRADIENT_CONIC;2\[&Setting]\30% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]50% 50%::GRADIENT_CONIC;2\[&Setting]\50% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]70% 50%::GRADIENT_CONIC;2\[&Setting]\70% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]85% 50%::GRADIENT_CONIC;2\[&Setting]\75% 50%\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
    --+|[c][rbutton]15% 75%::GRADIENT_CONIC;2\[&Setting]\15% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]30% 75%::GRADIENT_CONIC;2\[&Setting]\30% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]50% 75%::GRADIENT_CONIC;2\[&Setting]\50% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]70% 75%::GRADIENT_CONIC;2\[&Setting]\70% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton]&nbsp;
           [rbutton]85% 75%::GRADIENT_CONIC;2\[&Setting]\75% 75%\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c]
  --X|

  --:GC_STEP2|Ask for Color and keep asking till done
    --#title|CONIC Gradient
    --#leftsub|Step 2
    --#rightsub|List of Colors (2+)

    --&Setting|+ [&AddParam],
    --+setting:|[&Setting][hr]

    --+|[br]
    --+|Select your colors: Pick at least 2, but you can pick more to create a multi-color effect.
        
    --&reentryval|BLANK\[&Setting]\CONIC\[&PARAM]\[&RETURN_FUNCTION]
    -->GET_GRADIENT_COLOR|

  --/| Step 3: Done
  --:GC_STEP3|Step 3: Done
    --~Setting|string;trim;[&Setting]
    --~Len|string;length;[&Setting]
    --=Len|[$Len.Text] - 1
    --~Setting|string;left;[$Len];[&Setting]
    --&Setting|+ )
    --#[&PARAM]|[&Setting]
    --+[&PARAM] set to:|[&Setting][hr]
    --^[&RETURN_FUNCTION]|

  --+|[hr][c][rbutton]Cancel::[&RETURN_FUNCTION][/rbutton][/c]

--X|

--/| =======================================================================
--:GRADIENT_LINEAR|Step; Settings
--/| This function will call itself multiple times (circular), building Circle Gradient background syntax as it does
  --~Arg|string;split;\;[&reentryval]
  --&Step|[&Arg1]
  --&Setting|[&Arg2]
  --&AddParam|[&Arg3]
  --&PARAM|[&Arg4]
  --&RETURN_FUNCTION|[&Arg5]

  -->LOG|2;GRADIENT_LINEAR;[&reentryval]  
  --&reentryval|

  --/|+GRADIENT_LINEAR RVs|**Step:**[&Step] **Setting:**[&Setting] **New Param:**[&AddParam]

  --C[&Step]|1:GL_STEP1|2:GL_STEP2|3:GL_STEP3|4:GL_STEP4

  --:GL_STEP1|Ask Angle or Right to Left or Top to Bottom
    --#title|Linear Gradient
    --#leftsub|Step 1
    --#rightsub|Angle

    --&Setting|linear-gradient(
    --+setting:|[&Setting][hr]
    --/| Provide list of angles (0deg, 30deg, 45deg, 60deg, ... 360deg )

      --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
      --&trStyle1|style="border-top:0px dashed black;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
      --&trStyle2|style="border:0px dashed black;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"
      --&tdStyle1|style="width:50%;text-align:center;background-color:#FFFFFF;font-size:100%"
      --&tdStyle2|style="width:15%;text-align:center;background-color:#FFFFFF;font-size:100%"

  --+|Select the direction of the gradient fill.  Left to Right works well for most ScriptCard uses.  However, you can specify angled fills.  
  --+|[br]

  --+|[t [&tStyle]]
      [tr [&trStyle1]]
          [td colspan=3 [&tdStyle1]][rbutton]Left to Right::GRADIENT_LINEAR;2\[&Setting]\to right\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td colspan=3 [&tdstyle1]][rbutton]Top to Bottom::GRADIENT_LINEAR;2\[&Setting]\to bottom\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
        [/tr][tr [&trStyle2]]
          [td [&tdStyle2]][rbutton]15deg::GRADIENT_LINEAR;2\[&Setting]\15deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td] 
          [td [&tdStyle2]][rbutton]30deg::GRADIENT_LINEAR;2\[&Setting]\30deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]45deg::GRADIENT_LINEAR;2\[&Setting]\45deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]60deg::GRADIENT_LINEAR;2\[&Setting]\60deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]75deg::GRADIENT_LINEAR;2\[&Setting]\75deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]90deg::GRADIENT_LINEAR;2\[&Setting]\90deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
        [/tr][tr [&trStyle2]]
          [td [&tdStyle2]][rbutton]105deg::GRADIENT_LINEAR;2\[&Setting]\105deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td] 
          [td [&tdStyle2]][rbutton]120deg::GRADIENT_LINEAR;2\[&Setting]\120deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]135deg::GRADIENT_LINEAR;2\[&Setting]\135deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]150deg::GRADIENT_LINEAR;2\[&Setting]\150deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]165deg::GRADIENT_LINEAR;2\[&Setting]\165deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]180deg::GRADIENT_LINEAR;2\[&Setting]\180deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
        [/tr][tr [&trStyle2]]
          [td [&tdStyle2]][rbutton]195deg::GRADIENT_LINEAR;2\[&Setting]\195deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td] 
          [td [&tdStyle2]][rbutton]210deg::GRADIENT_LINEAR;2\[&Setting]\210deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]225deg::GRADIENT_LINEAR;2\[&Setting]\225deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]240deg::GRADIENT_LINEAR;2\[&Setting]\240deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]255deg::GRADIENT_LINEAR;2\[&Setting]\255deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]270deg::GRADIENT_LINEAR;2\[&Setting]\270deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
        [/tr][tr [&trStyle2]]
          [td [&tdStyle2]][rbutton]285deg::GRADIENT_LINEAR;2\[&Setting]\285deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td] 
          [td [&tdStyle2]][rbutton]300deg::GRADIENT_LINEAR;2\[&Setting]\300deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]315deg::GRADIENT_LINEAR;2\[&Setting]\315deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]330deg::GRADIENT_LINEAR;2\[&Setting]\330deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]345deg::GRADIENT_LINEAR;2\[&Setting]\345deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
          [td [&tdStyle2]][rbutton]360deg::GRADIENT_LINEAR;2\[&Setting]\360deg\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
        [/tr][/t]
  --X|

  --:GL_STEP2|Ask for Color and keep asking till done
    --#title|Linear Gradient
    --#leftsub|Step 2
    --#rightsub|List of Colors (2+)

    --&Setting|+ [&AddParam],
    --+setting:|[&Setting][hr]

    --+|[br]
    --+|Select your colors: Pick at least 2, but you can pick more to create a tri-color fade or rainbow effect.
        
    --&reentryval|BLANK\[&Setting]\LINEAR\[&PARAM]\[&RETURN_FUNCTION]
    -->GET_GRADIENT_COLOR|
  --X|

  --:GL_STEP3|Done - Set the new paramter and go back to top
    --~Setting|string;trim;[&Setting]
    --~Len|string;length;[&Setting]
    --=Len|[$Len.Text] - 1
    --~Setting|string;left;[$Len];[&Setting]
    --&Setting|+ )
    --#[&PARAM]|[&Setting]
    --+[&PARAM] set to:|[&Setting][hr]
    --^[&RETURN_FUNCTION]|
  --+|[hr][c][rbutton]Cancel::[&RETURN_FUNCTION][/rbutton][/c]
  --X|

--/| =======================================================================
--:GET_GRADIENT_HEX_COLOR|
  --~Arg|string;split;\;[&reentryval]
  --&ColorFam|[&Arg1]
  --&Setting|[&Arg2]
  --&Mode|[&Arg3]
  --&PARAM|[&Arg4]
  --&RETURN_FUNCTION|[&Arg5]

  -->LOG|2;GET_GRADIENT_HEX_COLOR;[&reentryval]  

    --I;Manually Enter Radient HEX Color|q;Clr;HEX Color (fmt: #123456)?
    --~Clr|string;replaceall;#;;[&Clr]
    --&Clr|#[&Clr]

    --?[&Mode] -eq LINEAR|GGHC_LINEAR
    --?[&Mode] -eq CONIC|GGHC_RADIAL
    --?[&Mode] -eq RADIAL|GGHC_RADIAL

    --:GGCH_LINEAR|
      --&reentryval|2\[&Setting]\[&Clr]\[&PARAM]\[&RETURN_FUNCTION]
      --^GRADIENT_LINEAR|
    --:GGCH_RADIAL|
      --&reentryval|4\[&Setting]\[&Clr]\[&PARAM]\[&RETURN_FUNCTION]
      --^GRADIENT_RADIAL|
    --:GGCH_CONIC|
      --&reentryval|4\[&Setting]\[&Clr]\[&PARAM]\[&RETURN_FUNCTION]
      --^GRADIENT_CONIC|
--X|

--/| =======================================================================
--:GET_GRADIENT_COLOR|
  --~Arg|string;split;\;[&reentryval]
  --&ColorFam|[&Arg1]
  --&Setting|[&Arg2]
  --&Mode|[&Arg3]
  --&PARAM|[&Arg4]
  --&RETURN_FUNCTION|[&Arg5]
  -->LOG|2;GET_GRADIENT_COLOR;[&reentryval]  

  --&reentryval|

  --/|+GRADIENT_LINEAR RVs|**ColorFam:**[&ColorFam] **Setting:**[&Setting] **Mode:**[&Mode]

    -->LOAD_COLOR_ARRAYS|
    --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
    --&trStyle1|style="border-top:0px dashed black;text-align:center;"
    --&trStyle1|style="border-top:0px dashed black;text-align:center;background-color:[&evenrowbackground]; color:[&evenrowfontcolor];"
    --&trStyle2|style="border:0px dashed black;text-align:center;background-color:[&oddrowbackground]; color:[&oddrowfontcolor];"



    --+|[c][rbutton]Manually Enter a HEX Color (#123456)::GET_GRADIENT_HEX_COLOR;BLANK\[&Setting]\[&Mode][/rbutton][/c][br]

    --+|[c][t [&tStyle]][tr [&trStyle1]][td][rbutton]Red::GET_GRADIENT_COLOR;Red\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]Pink::GET_GRADIENT_COLOR;Pink\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]Orange::GET_GRADIENT_COLOR;Orange\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]Yellow::GET_GRADIENT_COLOR;Yellow\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]Purple::GET_GRADIENT_COLOR;Purple\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [/tr][tr [&trStyle1]]
               [td][rbutton]Green::GET_GRADIENT_COLOR;Green\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]Blue::GET_GRADIENT_COLOR;Blue\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]Brown::GET_GRADIENT_COLOR;Brown\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]White::GET_GRADIENT_COLOR;White\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
               [td][rbutton]Gray::GET_GRADIENT_COLOR;Gray\[&Setting]\[&Mode]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
        [/tr][/t][/c]

    
    --?[&ColorFam] -eq BLANK|GGC_FOOTER

    --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal;"
    --&trStyle1|style="border-top:2px dashed black;font-size:150%"
    --&trStyle2|style="border-top:0px dashed black;font-size:100%"
    --&Tbl|[t [&tStyle]][tr [&trStyle1]][td Colspan=3][c][&ColorFam] Family of Colors[/c][/td][/tr]

    --~Clr|array;getfirst;ary[&ColorFam]
    --:GGC_COLOR_LOOP_START|
      --?[&Clr] -eq ArrayError|GGC_COLOR_LOOP_END

          --&Tbl|+ [tr [&trStyle2]]
          --?[&Mode] -eq LINEAR|GGC_LINEAR
          --?[&Mode] -eq CONIC|GGC_RADIAL
          --?[&Mode] -eq RADIAL|GGC_RADIAL
            --^TOP|

          --:GGC_LINEAR|
            --&Tbl|+[td style="text-align:center;background-color:[&Clr];"][rbutton]Set::GRADIENT_LINEAR;2\[&Setting]\[&Clr]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
            --^GGC_CONTINUE|
          --:GGC_RADIAL|
            --&Tbl|+[td style="text-align:center;background-color:[&Clr];"][rbutton]Set::GRADIENT_RADIAL;4\[&Setting]\[&Clr]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
            --^GGC_CONTINUE|
          --:GGC_CONIC|
            --&Tbl|+[td style="text-align:center;background-color:[&Clr];"][rbutton]Set::GRADIENT_CONIC;2\[&Setting]\[&Clr]\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
            --^GGC_CONTINUE|

          --:GGC_CONTINUE|
          --&Tbl|+[td style="color:black;background-color:[&Clr];"][c][b][&Clr][/b][/c][/td] 
          --&Tbl|+[td style="color:yellow;background-color:[&Clr];"][c][b][&Clr][/b][/c][/td][/tr]    

        --~Clr|array;getnext;ary[&ColorFam]
        --^GGC_COLOR_LOOP_START|
    --:GGC_COLOR_LOOP_END|
    --&Tbl|+ [/t]
    --+|[&Tbl]

    --:GGC_FOOTER|
    --?[&Mode] -eq LINEAR|GGC_LINEAR_DONE
    --?[&Mode] -eq RADIAL|GGC_RADIAL_DONE
    --?[&Mode] -eq CONIC|GGC_CONIC_DONE
      --:GGC_LINEAR_DONE|
        --+|[hr][c][rbutton]Done Picking Colors::GRADIENT_LINEAR;3\[&Setting]\\[&PARAM]\[&RETURN_FUNCTION][/rbutton][rbutton]Cancel::[&RETURN_FUNCTION][/rbutton][/c]
        --X|
      --:GGC_RADIAL_DONE|
        --+|[hr][c][rbutton]Done Picking Colors::GRADIENT_RADIAL;5\[&Setting]\\[&PARAM]\[&RETURN_FUNCTION][/rbutton][rbutton]Cancel::[&RETURN_FUNCTION][/rbutton][/c]
        --X|
      --:GGC_CONIC_DONE|
        --+|[hr][c][rbutton]Done Picking Colors::GRADIENT_CONIC;3\[&Setting]\\[&PARAM]\[&RETURN_FUNCTION][/rbutton][rbutton]Cancel::[&RETURN_FUNCTION][/rbutton][/c]
        --X|

--/| =======================================================================
--:SHOW_SAVED_SCHEMAS|
  -->LOG|2;SHOW_SAVED_SETTINGS;
  --@sc-liststoredsettings|
  --^TOP|

--/| =======================================================================
--:LOAD_SAVED_SCHEMA|
  --~Arg|string;split;\;[&reentryval]
  --&SName|[&Arg1]
  -->LOG|2;LOAD_SAVED_SCHEMA;[&reentryval]

  --?x[&SName] -ne x|SCS_LOAD_SCHEMA
    --I;Load Saved Schema/Settings?|q;SName;Saved Schema/Settings name to load?

  --:SCS_LOAD_SCHEMA|
  --?x[&SName] -eq x|LSS_INVALID_NAME
    --Lsettings|[&SName]

    --^TOP|
  --:LSS_INVALID_NAME|
    --+ERROR|Invalid Setting Name
    --+|[HR]
    --^TOP|

--/| =======================================================================
--:SAVE_CURRENT_SCHEMA|
  --~Arg|string;split;\;[&reentryval]
  --&SName|[&Arg1]
  -->LOG|2;SAVE_CURRENT_SCHEMA;[&reentryval]

  --?x[&SName] -ne x|SCS_SAVE_SCHEMA
    --I;Save Current Schema Name?|q;SName;Saved Schema name?

  --:SCS_SAVE_SCHEMA|
  --?x[&SName] -eq x|SCS_INVALID_NAME

    --#leftsub|
    --#rightsub|
    --#title|

    --Ssettings|[&SName]
    --^TOP|
  --:SCS_INVALID_NAME|
    --+ERROR|Invalid Setting Name
    --+|[HR]
    --^TOP|
--X|

--/| =======================================================================
--:WEB_LINK|Name; Ref 
  --&LINK|[button][%1%]::[%2%][/button]
--<|

--/| =======================================================================
--:SET_COLOR_BY_NAME_REENTRY|
  --~Arg|string;split;\;[&reentryval]
  --&ColorFam|[&Arg1]
  --&PARAM|[&Arg2]
  --&RETURN_FUNCTION|[&Arg3]
  -->LOG|2;SET_COLOR_BY_NAME_REENTRY;[&reentryval]
  -->SET_COLOR_BY_NAME|[&ColorFam];[&PARAM];[&RETURN_FUNCTION]
  --X|


--/| =======================================================================
--:SET_COLOR_BY_NAME|Color_Family; Parameter being set
  --/|-~Arg|string;split;\;[&reentryval]
  --&ColorFam|[%1%]
  --&PARAM|[%2%]
  --&RETURN_FUNCTION|[%3%]
  -->LOG|2;SET_COLOR_BY_NAME;[%1%], [%2%], [%3%]

  --#Title|Select Color 
  --#leftSub|[&PARAM]
  --#RightSub|[&ColorFam]

  -->LOAD_COLOR_ARRAYS|


  --+|[c][rbutton]Manually Enter a HEX Color (#123456)::SET_HEX_COLOR;[&PARAM]\[&RETURN_FUNCTION][/rbutton][/c][br]

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal; table-layout: fixed;"
  --&trStyle1|style="border-top:0px dashed black;text-align:center;"


  --+|[c][t [&tStyle]][tr [&trStyle1]]
             [td][rbutton]Red::SET_COLOR_BY_NAME_REENTRY;Red\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]Pink::SET_COLOR_BY_NAME_REENTRY;Pink\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]Orange::SET_COLOR_BY_NAME_REENTRY;Orange\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]Yellow::SET_COLOR_BY_NAME_REENTRY;Yellow\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]Purple::SET_COLOR_BY_NAME_REENTRY;Purple\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [/tr][tr [&trStyle1]]
             [td][rbutton]Green::SET_COLOR_BY_NAME_REENTRY;Green\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]Blue::SET_COLOR_BY_NAME_REENTRY;Blue\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]Brown::SET_COLOR_BY_NAME_REENTRY;Brown\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]White::SET_COLOR_BY_NAME_REENTRY;White\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
             [td][rbutton]Gray::SET_COLOR_BY_NAME_REENTRY;Gray\[&PARAM]\[&RETURN_FUNCTION][/rbutton][/td]
      [/tr][/t][/c]

  --?[&ColorFam] -eq BLANK|GGC_FOOTER

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;white-space: normal;"
  --&trStyle1|style="border-top:2px dashed black;font-size:150%"
  --&trStyle2|style="border-top:0px dashed black;font-size:100%"

  --&Tbl|[t [&tStyle]][tr [&trStyle1]][td Colspan=3][c][&ColorFam] Family of Colors[/c][/td][/tr]

  --~Clr|array;getfirst;ary[&ColorFam]

  --:COLOR_LOOP_START|
    --?[&Clr] -eq ArrayError|COLOR_LOOP_END
        --&Tbl|+ [tr [&trStyle2]][td style="text-align:center;background-color:[&Clr];"][rbutton]Set::SET_PARAM_COLOR;[&Clr]\[&PARAM]\[&RETURN_FUNCTION][/rbutton]
            [td style="color:black;background-color:[&Clr];"][c][b][&Clr][/b][/c][/td] 
            [td style="color:yellow;background-color:[&Clr];"][c][b][&Clr][/b][/c][/td][/tr]    
      --~Clr|array;getnext;ary[&ColorFam]
      --^COLOR_LOOP_START|
  --:COLOR_LOOP_END|
  --&Tbl|+ [/t]
  --+|[&Tbl]

  --:SCBN_FOOTER|

  --+|[hr][c][rbutton]Return::[&RETURN_FUNCTION][/rbutton][/c]

--X|

--/| =======================================================================
--:SET_PARAM_COLOR|
  --~Arg|string;split;\;[&reentryval]
  --&ColorName|[&Arg1]
  --&PARAM|[&Arg2]
  --&RETURN_FUNCTION|[&Arg3]
  -->LOG|2;SET_PARAM_COLOR;[&reentryval]

  --#[&PARAM]|[&ColorName]
  --+[&PARAM] set to:|[&ColorName]
  --+|[HR]

  --^[&RETURN_FUNCTION]|

--/| =======================================================================
--:SET_HEX_COLOR|Color_Family; Parameter being set, RETURN_FUNCTION
  --~Arg|string;split;\;[&reentryval]
  --&PARAM|[&Arg1]
  --&RETURN_FUNCTION|[&Arg2]
  -->LOG|2;SET_HEX_COLOR;[&reentryval]
  
  --#Title|Select Color by HEX Value
  --#leftSub|[&PARAM]
  --#RightSub|Hex Color Format #123456

  --I;Set Hex Color for [&PARAM]|q;Clr;Set Hex Color for [&PARAM]: (fmt: #123456)?
  --~Clr|string;replaceall;#;;[&Clr]
  --#[&PARAM]|#[&Clr]
  --+[&PARAM] set to:|#[&Clr]
  --+|[HR]    

  --^[&RETURN_FUNCTION]|

--/| =======================================================================
--:LOAD_COLOR_ARRAYS|
  -->LOG|3;LOAD_COLOR_ARRAYS;
  --~|array;define;aryRed;IndianRed;LightCoral;Salmon;DarkSalmon;LightSalmon;Crimson;Red;FireBrick;DarkRed
  --~|array;define;aryPink;Pink;LightPink;HotPink;DeepPink;MediumVioletRed;PaleVioletRed
  --~|array;define;aryOrange;LightSalmon;Coral;Tomato;OrangeRed;DarkOrange;Orange
  --~|array;define;aryYellow;Gold;Yellow;LightYellow;LemonChiffon;LightGoldenrodYellow;PapayaWhip;Moccasin;PeachPuff;PaleGoldenrod;Khaki;DarkKhaki
  --~|array;define;aryPurple;Lavender;Thistle;Plum;Violet;Orchid;Fuchsia;Magenta;MediumOrchid;MediumPurple;RebeccaPurple;BlueViolet;DarkViolet;DarkOrchid;DarkMagenta;Purple;Indigo;SlateBlue;DarkSlateBlue;MediumSlateBlue
  --~|array;define;aryGreen;GreenYellow;Chartreuse;LawnGreen;Lime;LimeGreen;PaleGreen;LightGreen;MediumSpringGreen;SpringGreen;MediumSeaGreen;SeaGreen;ForestGreen;Green;DarkGreen;YellowGreen;OliveDrab;Olive;DarkOliveGreen;MediumAquamarine;DarkSeaGreen;LightSeaGreen;DarkCyan;Teal
  --~|array;define;aryBlue;Aqua;Cyan;LightCyan;PaleTurquoise;Aquamarine;Turquoise;MediumTurquoise;DarkTurquoise;CadetBlue;SteelBlue;LightSteelBlue;PowderBlue;LightBlue;SkyBlue;LightSkyBlue;DeepSkyBlue;DodgerBlue;CornflowerBlue;MediumSlateBlue;RoyalBlue;Blue;MediumBlue;DarkBlue;Navy;MidnightBlue
  --~|array;define;aryBrown;Cornsilk;BlanchedAlmond;Bisque;NavajoWhite;Wheat;BurlyWood;Tan;RosyBrown;SandyBrown;Goldenrod;DarkGoldenrod;Peru;Chocolate;SaddleBrown;Sienna;Brown;Maroon
  --~|array;define;aryWhite;White;Snow;HoneyDew;MintCream;Azure;AliceBlue;GhostWhite;WhiteSmoke;SeaShell;Beige;OldLace;FloralWhite;Ivory;AntiqueWhite;Linen;LavenderBlush;MistyRose
  --~|array;define;aryGray;Gainsboro;LightGray;Silver;DarkGray;Gray;DimGray;LightSlateGray;SlateGray;DarkSlateGray;Black
--<|

--/| =======================================================================
--:SELECT_TIME_ZONE|
  -->LOG|2;SELECT_TIME_ZONE;
  --&TZUS|US/Alaska^US/Aleutian^US/Arizona^US/Central^US/East-Indiana^US/Eastern^US/Hawaii^US/Indiana-Starke^US/Michigan^US/Mountain^US/Pacific
  --&TZAmericas|America/Adak^America/Anchorage^America/Anguilla^America/Antigua^America/Araguaina^America/Argentina/Buenos_Aires^America/Argentina/Catamarca^America/Argentina/ComodRivadavia^America/Argentina/Cordoba^America/Argentina/Jujuy^America/Argentina/La_Rioja^America/Argentina/Mendoza^America/Argentina/Rio_Gallegos^America/Argentina/Salta^America/Argentina/San_Juan^America/Argentina/San_Luis^America/Argentina/Tucuman^America/Argentina/Ushuaia^America/Aruba^America/Asuncion^America/Atikokan^America/Atka^America/Bahia^America/Bahia_Banderas^America/Barbados^America/Belem^America/Belize^America/Blanc-Sablon^America/Boa_Vista^America/Bogota^America/Boise^America/Buenos_Aires^America/Cambridge_Bay^America/Campo_Grande^America/Cancun^America/Caracas^America/Catamarca^America/Cayenne^America/Cayman^America/Chicago^America/Chihuahua^America/Coral_Harbour^America/Cordoba^America/Costa_Rica^America/Creston^America/Cuiaba^America/Curacao^America/Danmarkshavn^America/Dawson^America/Dawson_Creek^America/Denver^America/Detroit^America/Dominica^America/Edmonton^America/Eirunepe^America/El_Salvador^America/Ensenada^America/Fort_Nelson^America/Fort_Wayne^America/Fortaleza^America/Glace_Bay^America/Godthab^America/Goose_Bay^America/Grand_Turk^America/Grenada^America/Guadeloupe^America/Guatemala^America/Guayaquil^America/Guyana^America/Halifax^America/Havana^America/Hermosillo^America/Indiana/Indianapolis^America/Indiana/Knox^America/Indiana/Marengo^America/Indiana/Petersburg^America/Indiana/Tell_City^America/Indiana/Vevay^America/Indiana/Vincennes^America/Indiana/Winamac^America/Indianapolis^America/Inuvik^America/Iqaluit^America/Jamaica^America/Jujuy^America/Juneau^America/Kentucky/Louisville^America/Kentucky/Monticello^America/Knox_IN^America/Kralendijk^America/La_Paz^America/Lima^America/Los_Angeles^America/Louisville^America/Lower_Princes^America/Maceio^America/Managua^America/Manaus^America/Marigot^America/Martinique^America/Matamoros^America/Mazatlan^America/Mendoza^America/Menominee^America/Merida^America/Metlakatla^America/Mexico_City^America/Miquelon^America/Moncton^America/Monterrey^America/Montevideo^America/Montreal^America/Montserrat^America/Nassau^America/New_York^America/Nipigon^America/Nome^America/Noronha^America/North_Dakota/Beulah^America/North_Dakota/Center^America/North_Dakota/New_Salem^America/Nuuk^America/Ojinaga^America/Panama^America/Pangnirtung^America/Paramaribo^America/Phoenix^America/Port-au-Prince^America/Port_of_Spain^America/Porto_Acre^America/Porto_Velho^America/Puerto_Rico^America/Punta_Arenas^America/Rainy_River^America/Rankin_Inlet^America/Recife^America/Regina^America/Resolute^America/Rio_Branco^America/Rosario^America/Santa_Isabel^America/Santarem^America/Santiago^America/Santo_Domingo^America/Sao_Paulo^America/Scoresbysund^America/Shiprock^America/Sitka^America/St_Barthelemy^America/St_Johns^America/St_Kitts^America/St_Lucia^America/St_Thomas^America/St_Vincent^America/Swift_Current^America/Tegucigalpa^America/Thule^America/Thunder_Bay^America/Tijuana^America/Toronto^America/Tortola^America/Vancouver^America/Virgin^America/Whitehorse^America/Winnipeg^America/Yakutat^America/Yellowknife
  --&TZAfrica|Africa/Abidjan^Africa/Accra^Africa/Addis_Ababa^Africa/Algiers^Africa/Asmara^Africa/Asmera^Africa/Bamako^Africa/Bangui^Africa/Banjul^Africa/Bissau^Africa/Blantyre^Africa/Brazzaville^Africa/Bujumbura^Africa/Cairo^Africa/Casablanca^Africa/Ceuta^Africa/Conakry^Africa/Dakar^Africa/Dar_es_Salaam^Africa/Djibouti^Africa/Douala^Africa/El_Aaiun^Africa/Freetown^Africa/Gaborone^Africa/Harare^Africa/Johannesburg^Africa/Juba^Africa/Kampala^Africa/Khartoum^Africa/Kigali^Africa/Kinshasa^Africa/Lagos^Africa/Libreville^Africa/Lome^Africa/Luanda^Africa/Lubumbashi^Africa/Lusaka^Africa/Malabo^Africa/Maputo^Africa/Maseru^Africa/Mbabane^Africa/Mogadishu^Africa/Monrovia^Africa/Nairobi^Africa/Ndjamena^Africa/Niamey^Africa/Nouakchott^Africa/Ouagadougou^Africa/Porto-Novo^Africa/Sao_Tome^Africa/Timbuktu^Africa/Tripoli^Africa/Tunis^Africa/Windhoek
  --&TZAsia|Asia/Aden^Asia/Almaty^Asia/Amman^Asia/Anadyr^Asia/Aqtau^Asia/Aqtobe^Asia/Ashgabat^Asia/Ashkhabad^Asia/Atyrau^Asia/Baghdad^Asia/Bahrain^Asia/Baku^Asia/Bangkok^Asia/Barnaul^Asia/Beirut^Asia/Bishkek^Asia/Brunei^Asia/Calcutta^Asia/Chita^Asia/Choibalsan^Asia/Chongqing^Asia/Chungking^Asia/Colombo^Asia/Dacca^Asia/Damascus^Asia/Dhaka^Asia/Dili^Asia/Dubai^Asia/Dushanbe^Asia/Famagusta^Asia/Gaza^Asia/Harbin^Asia/Hebron^Asia/Ho_Chi_Minh^Asia/Hong_Kong^Asia/Hovd^Asia/Irkutsk^Asia/Istanbul^Asia/Jakarta^Asia/Jayapura^Asia/Jerusalem^Asia/Kabul^Asia/Kamchatka^Asia/Karachi^Asia/Kashgar^Asia/Kathmandu^Asia/Katmandu^Asia/Khandyga^Asia/Kolkata^Asia/Krasnoyarsk^Asia/Kuala_Lumpur^Asia/Kuching^Asia/Kuwait^Asia/Macao^Asia/Macau^Asia/Magadan^Asia/Makassar^Asia/Manila^Asia/Muscat^Asia/Nicosia^Asia/Novokuznetsk^Asia/Novosibirsk^Asia/Omsk^Asia/Oral^Asia/Phnom_Penh^Asia/Pontianak^Asia/Pyongyang^Asia/Qatar^Asia/Qostanay^Asia/Qyzylorda^Asia/Rangoon^Asia/Riyadh^Asia/Saigon^Asia/Sakhalin^Asia/Samarkand^Asia/Seoul^Asia/Shanghai^Asia/Singapore^Asia/Srednekolymsk^Asia/Taipei^Asia/Tashkent^Asia/Tbilisi^Asia/Tehran^Asia/Tel_Aviv^Asia/Thimbu^Asia/Thimphu^Asia/Tokyo^Asia/Tomsk^Asia/Ujung_Pandang^Asia/Ulaanbaatar^Asia/Ulan_Bator^Asia/Urumqi^Asia/Ust-Nera^Asia/Vientiane^Asia/Vladivostok^Asia/Yakutsk^Asia/Yangon^Asia/Yekaterinburg^Asia/Yerevan
  --&TZAtlantic|Atlantic/Azores^Atlantic/Bermuda^Atlantic/Canary^Atlantic/Cape_Verde^Atlantic/Faeroe^Atlantic/Faroe^Atlantic/Jan_Mayen^Atlantic/Madeira^Atlantic/Reykjavik^Atlantic/South_Georgia^Atlantic/St_Helena^Atlantic/Stanley
  --&TZAustrailia|Australia/ACT^Australia/Adelaide^Australia/Brisbane^Australia/Broken_Hill^Australia/Canberra^Australia/Currie^Australia/Darwin^Australia/Eucla^Australia/Hobart^Australia/LHI^Australia/Lindeman^Australia/Lord_Howe^Australia/Melbourne^Australia/North^Australia/NSW^Australia/Perth^Australia/Queensland^Australia/South^Australia/Sydney^Australia/Tasmania^Australia/Victoria^Australia/West^Australia/Yancowinna
  --&TZEurope|Europe/Amsterdam^Europe/Andorra^Europe/Astrakhan^Europe/Athens^Europe/Belfast^Europe/Belgrade^Europe/Berlin^Europe/Bratislava^Europe/Brussels^Europe/Bucharest^Europe/Budapest^Europe/Busingen^Europe/Chisinau^Europe/Copenhagen^Europe/Dublin^Europe/Gibraltar^Europe/Guernsey^Europe/Helsinki^Europe/Isle_of_Man^Europe/Istanbul^Europe/Jersey^Europe/Kaliningrad^Europe/Kiev^Europe/Kirov^Europe/Lisbon^Europe/Ljubljana^Europe/London^Europe/Luxembourg^Europe/Madrid^Europe/Malta^Europe/Mariehamn^Europe/Minsk^Europe/Monaco^Europe/Moscow^Europe/Nicosia^Europe/Oslo^Europe/Paris^Europe/Podgorica^Europe/Prague^Europe/Riga^Europe/Rome^Europe/Samara^Europe/San_Marino^Europe/Sarajevo^Europe/Saratov^Europe/Simferopol^Europe/Skopje^Europe/Sofia^Europe/Stockholm^Europe/Tallinn^Europe/Tirane^Europe/Tiraspol^Europe/Ulyanovsk^Europe/Uzhgorod^Europe/Vaduz^Europe/Vatican^Europe/Vienna^Europe/Vilnius^Europe/Volgograd^Europe/Warsaw^Europe/Zagreb^Europe/Zaporozhye^Europe/Zurich^Factory^GB^GB-Eire
  --&TZGeneral|Etc/GMT^Etc/GMT+1^Etc/GMT+2^Etc/GMT+3^Etc/GMT+4^Etc/GMT+5^Etc/GMT+6^Etc/GMT+7^Etc/GMT+8^Etc/GMT+9^Etc/GMT-1^Etc/GMT-10^Etc/GMT-11^Etc/GMT-12^Etc/GMT-13^Etc/GMT-14^Etc/GMT-2^Etc/GMT-3^Etc/GMT-4^Etc/GMT-5^Etc/GMT-6^Etc/GMT-7^Etc/GMT-8^Etc/GMT-9^Etc/GMT-10^Etc/GMT-11^Etc/GMT-12^Etc/GMT-13^Etc/GMT-14
  --&TZIndian|Indian/Antananarivo^Indian/Chagos^Indian/Christmas^Indian/Cocos^Indian/Comoro^Indian/Kerguelen^Indian/Mahe^Indian/Maldives^Indian/Mauritius^Indian/Mayotte^Indian/Reunion
  --&TZPacific|Africa/Banjul^Africa/Bissau^Africa/Blantyre^Africa/Brazzaville^Africa/Bujumbura^Africa/Cairo^Africa/Casablanca^Africa/Ceuta^Africa/Conakry^Africa/Dakar^Africa/Dar_es_Salaam^Africa/Djibouti^Africa/Douala^Africa/El_Aaiun^Africa/Freetown^Africa/Gaborone^Africa/Harare^Africa/Johannesburg^Africa/Juba^Africa/Kampala^Africa/Khartoum^Africa/Kigali^Africa/Kinshasa^Africa/Lagos^Africa/Libreville^Africa/Lome^Africa/Luanda^Africa/Lubumbashi^Africa/Lusaka^Africa/Malabo^Africa/Maputo^Africa/Maseru^Africa/Mbabane^Africa/Mogadishu^Africa/Monrovia^Africa/Nairobi^Africa/Ndjamena^Africa/Niamey^Africa/Nouakchott^Africa/Ouagadougou^Africa/Porto-Novo^Africa/Sao_Tome^Africa/Timbuktu

  --I;Select Timezone Region|q;Region;Timezone Region?|General|Americas|Africa|Asia|Atlantic|Australia|Europe|Indian|Pacific

  --?[&Region] -eq General|&TZList;[&TZGeneral]
  --?[&Region] -eq US|&TZList;[&TZUS]
  --?[&Region] -eq Americas|&TZList;[&TZAmericas]
  --?[&Region] -eq Africa|&TZList;[&TZAfrica]
  --?[&Region] -eq Atlantic|&TZList;[&TZAtlantic]
  --?[&Region] -eq Austrailia|&TZList;[&TZAustrailia]
  --?[&Region] -eq Europe|&TZList;[&TZEurope]
  --?[&Region] -eq Indian|&TZList;[&TZIndian]
  --?[&Region] -eq Pacific|&TZList;[&TZPacific]

  --~TZList|string;replaceall;^;|;[&TZList] 
  --/|*List|[&TZList]

  --I;Select Timezone|q;TZ;Timezone?|[&TZList]
  --#timezone|[&TZ]
  --+TimeZone set to:|[&TZ]
  --^EMOTE_SETTINGS|
--<|

--/| =======================================================================
--:EXAMPLE_CARD|
  --#Title|Example Card
  --#leftSub|Fireball!!!
  --#rightSub|Duck!!!
  -->LOG|2;EXAMPLE_CARD;

  --+|[c][b] === You've been Fireballed === [/b][/c]
  --+|[br]
  --+|[b][i]Fireball is cast in your general direction...[/i][/b]
  --+|[br]
  --+|A bright streak flashes from your pointing finger to a point you choose within range and then blossoms with a low roar into an explosion of flame. Each creature in a 20-foot-radius sphere centered on that point must make a Dexterity saving throw. A target takes *8d6* fire damage on a failed save, or half as much damage on a successful one.
  --+|[br]
  --=Dmg|8d6
  --=HalfDmg|[$Dmg]\2
  --+Save|DC16 Dexterity Saving Throw
  --=ST|1d20+3
  --&SaveMsg|OUCH!!! That hurt.  Maybe next time move a little faster. ***(Saving Throw: [$ST])***
  --?[$ST.Total] -lt 16|EXP_FAIL
      --=Dmg|[$Dmg]\2
      --&SaveMsg|Wow - That was was painful, but could have been worse. ***(Saving Throw: [$ST])***
  --:EXP_FAIL|
  --+Roll|[&SaveMsg]
  --+Damage|You take [$Dmg] in Fire Damage.  
  --+|[hr]
  --+|[c][b]A bunch of dice![/b][/c]
  --+|[c][d4]4[/d4][d6]6[/d6][d8]8[/d8][d10]10[/d10][d12]12[/d12][d20]20[/d20][/c]
  --+|[hr][c][rbutton]Return::TOP[/rbutton][/c]
--X|

--/| =======================================================================
--:DUMP_CURRENT_SETTINGS|
  -->LOG|2;DUMP_CURRENT_SETTINGS;
  -->LOAD_CURRENT_SETTINGS|

  --+&#45;&#45;&#35;title&#124;|[&title]
  --+&#45;&#45;&#35;titlecardbackground&#124;|[&titlecardbackground]
  --+&#45;&#45;&#35;titlefontsize&#124;|[&titlefontsize]
  --+&#45;&#45;&#35;titlefontlineheight&#124;|[&titlefontlineheight]
  --+&#45;&#45;&#35;titlefontface&#124;|[&titlefontface]
  --+&#45;&#45;&#35;titlecardgradient&#124;|[&titlecardgradient]
  --+&#45;&#45;&#35;titlecardbackgroundimage&#124;|[&titlecardbackgroundimage]  
  --+&#45;&#45;&#35;norollhighlight&#124;|[&norollhighlight]
  --+&#45;&#45;&#35;subtitleseparator&#124;|[&subtitleseparator]
  --+&#45;&#45;&#35;hideTitleCard&#124;|[&hidetitlecard]
  --+&#45;&#45;&#35;bodyfontsize&#124;|[&bodyfontsize]
  --+&#45;&#45;&#35;bodyfontface&#124;|[&bodyfontface]
  --+&#45;&#45;&#35;oddrowbackground&#124;|[&oddrowbackground]
  --+&#45;&#45;&#35;oddrowfontcolor&#124;|[&oddrowfontcolor]
  --+&#45;&#45;&#35;evenrowbackground&#124;|[&evenrowbackground]
  --+&#45;&#45;&#35;evenrowfontcolor&#124;|[&evenrowfontcolor]
  --+&#45;&#45;&#35;buttonbackground&#124;|[&buttonbackground]
  --+&#45;&#45;&#35;buttontextcolor&#124;|[&buttontextcolor]
  --+&#45;&#45;&#35;buttonbordercolor&#124;|[&buttonbordercolor]
  --+&#45;&#45;&#35;buttonfontsize&#124;|[&buttonfontsize]
  --+&#45;&#45;&#35;buttonfontface&#124;|[&buttonfontface]
  --+&#45;&#45;&#35;dicefontcolor&#124;|[&dicefontcolor]
  --+&#45;&#45;&#35;dicefontsize&#124;|[&dicefontsize]
  --+&#45;&#45;&#35;usehollowdice&#124;|[&usehollowdice]
  --+&#45;&#45;&#35;emotebackground&#124;|[&emotebackground]
  --+&#45;&#45;&#35;emotestate&#124;|[&emotestate]
  --+&#45;&#45;&#35;emotetext&#124;|[&emotetext]
  --+&#45;&#45;&#35;tableborderradius&#124;|[&tableborderradius]
  --+&#45;&#45;&#35;tableshadow&#124;|[&tableshadow]
  --+&#45;&#45;&#35;debug&#124;|[&debug]
  --+&#45;&#45;&#35;whisper&#124;|[&whisper]

  --+&#45;&#45;&#35;bodybackgroundimage&#124;|[&bodybackgroundimage]
  --+&#45;&#45;&#35;evenrowbackgroundimage&#124;|[&evenrowbackgroundimage]
  --+&#45;&#45;&#35;oddrowbackgroundimage&#124;|[&oddrowbackgroundimage]
  --+&#45;&#45;&#35;buttonbackgroundimage&#124;|[&buttonbackgroundimage]


  --+|[hr][c][rbutton]Return::TOP[/rbutton][/c]
  --X|
--<|

--/| =======================================================================
--:LOAD_CURRENT_SETTINGS|
--/| Requires version 1.3.5 of scriptcards
  -->LOG|2;LOAD_CURRENT_SETTINGS;

  --/|New Settings SC 1.3.7
  --~bodybackgroundimage|system;readsetting;bodybackgroundimage
  --~evenrowbackgroundimage|system;readsetting;evenrowbackgroundimage
  --~oddrowbackgroundimage|system;readsetting;oddrowbackgroundimage
  --~buttonbackgroundimage|system;readsetting;buttonbackgroundimage

  --~title|system;readsetting;title
  --~titlecardbackground|system;readsetting;titlecardbackground
  --~titlefontsize|system;readsetting;titlefontsize
  --~titlefontlineheight|system;readsetting;titlefontlineheight
  --~titlefontface|system;readsetting;titlefontface
  --~titlecardgradient|system;readsetting;titlecardgradient
  --~titlecardbackgroundimage|system;readsetting;titlecardbackgroundimage
  --~norollhighlight|system;readsetting;norollhighlight  
  --~subtitleseparator|system;readsetting;subtitleSeperator
  --~hidetitlecard|system;readsetting;hideTitleCard
  --~bodyfontsize|system;readsetting;bodyfontsize
  --~bodyfontface|system;readsetting;bodyfontface
  --~oddrowbackground|system;readsetting;oddrowbackground
  --~oddrowfontcolor|system;readsetting;oddrowfontcolor
  --~evenrowbackground|system;readsetting;evenrowbackground
  --~evenrowfontcolor|system;readsetting;evenrowfontcolor
  --~buttonbackground|system;readsetting;buttonbackground
  --~buttontextcolor|system;readsetting;buttontextcolor
  --~buttonbordercolor|system;readsetting;buttonbordercolor
  --~buttonfontsize|system;readsetting;buttonfontsize
  --~buttonfontface|system;readsetting;buttonfontface
  --~dicefontcolor|system;readsetting;dicefontcolor
  --~dicefontsize|system;readsetting;dicefontsize
  --~usehollowdice|system;readsetting;usehollowdice
  --~emotebackground|system;readsetting;emotebackground
  --~emotetext|system;readsetting;emotetext
  --~emotestate|system;readsetting;emotestate
  --~tableborderradius|system;readsetting;tableborderradius    
  --~tableshadow|system;readsetting;tableshadow    
  --~debug|system;readsetting;debug    
  --~whisper|system;readsetting;whisper    
  --~timezone|system;readsetting;timezone
--<|

--/| =======================================================================
--:SECTION_HEADER|Title
  --~buttonbackground_orig|system;readsetting;buttonbackground
  --~buttontextcolor_orig|system;readsetting;buttontextcolor
  --~buttonbordercolor_orig|system;readsetting;buttonbordercolor
  --~buttonfontsize_orig|system;readsetting;buttonfontsize
  --~buttonfontface_orig|system;readsetting;buttonfontface

  --#buttonbackground|silver
  --#buttontextcolor|black
  --#buttonbordercolor|DarkSlateGray
  --#buttonfontsize|12
  --#buttonfontface|Arial

  --&hdrstyle_T|style="width:99%;padding:0px;border-spacing:0px;border-collapse:collapse;text-shadow: 1px 1px 1px black;border-top: 2px solid black;  table-layout: fixed;"
  --&hdrstyle_TR|style="border:0px solid DarkSlateGray;"
  --&hdrstyle_TD|style="width:100%;font-size:120%;color:black;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][%1%][/td][/tr][/t]

  --#buttonbackground|[&buttonbackground_orig]
  --#buttontextcolor|[&buttontextcolor_orig]
  --#buttonbordercolor|[&buttonbordercolor_orig]
  --#buttonfontsize|[&buttonfontsize_orig]
  --#buttonfontface|[&buttonfontface_orig]
--<|

--:LOG|debug level;hdr;msg
  --\|debug level is used to show more or less debug messages.  1 is high level, 2 is detailed ...
  --?[%1%] -gt [&gDEBUG]|LOG_EXIT
    --*[%2%]|[%3%]
  --:LOG_EXIT|
--<|


--:RESET|
  --/|>DEFAULT_SETTINGS|
  -->CLEAR_SETTINGS|
--^TOP|

--:DEFAULT_SETTINGS|
  --/|#titleCardBackground|#932729
  --#titlecardgradient|0

  --/|#titlecardbackgroundimage|url(https://s3.amazonaws.com/files.d20.io/images/229710089/fIxcDIjUWW3z2b6RZRGrCg/original.png?16242243555);background-size: auto;background-repeat: round;
  --#titlecardbackgroundimage|linear-gradient(to right, black, white);
  --/|#titlecardbackgroundimage|radial-gradient(circle, black, white);
  --/|#titlecardbackgroundimage|radial-gradient(circle at top right, black, red, yellow);
  --/|#titlecardbackgroundimage|linear-gradient(90deg, rgba(0,0,0,1) 0%, rgba(245,74,9,1) 59%, rgba(251,255,0,0.9262079831932774) 100%);
  --/|#titlecardbackgroundimage|linear-gradient(90deg, rgba(0,0,0,1) 0%, rgba(245,74,9,1) 59%, rgba(251,255,0,0.9262079831932774) 100%);
  --/|#titlecardbackgroundimage|linear-gradient(90deg, rgba(0,0,0,1) 0%, rgba(245,74,9,1) 59%, rgba(251,255,0,0.9262079831932774) 100%);  
  --/|#titlecardbackgroundimage|conic-gradient(red 50deg, yellow 100deg, lime 200deg, aqua, blue, magenta, red);
  --/|#titlecardbackgroundimage|conic-gradient(#ff8a00, #e52e71);
  --#titleFontSize|2.0em
  --#titleFontLineHeight|2.0em
  --#titleFontFace|Contrail One

  --#hideTitleCard|0
  --#subtitleSeperator|&nbsp;♦&nbsp;

  --#bodyFontSize|12px
  --#bodyFontFace|Helvetica
  --#bodybackgroundimage|

  --#oddrowbbackgroundimage|
  --#oddRowBackground|LightBlue
  --#oddRowFontColor|#000000
  --#evenrowbbackgroundimage|
  --#evenRowBackground|#FFFFFF
  --#evenRowFontColor|#000000  

  --#buttonbackgroundimage|
  --#buttonBackGround|Gainsboro
  --#buttonTextColor|#0905f2
  --#buttonBorderColor|#0905f2
  --#buttonFontSize|12px
  --#buttonFontFace|Tahoma

  --#diceFontColor|#1C6EA4
  --#dicefontsize|3.0em
  --#usehollowdice|0

  --#emoteBackground|#f5f5ba
  --#emoteText|This is emote text
  --#emoteState|visible

  --#tableBorderRadius|6px;
  --#tableShadow|5px 3px 3px 0px #aaa;

  --#debug|1
  --#whisper|
  --#timezone|America/Chicago
  -->LOAD_CURRENT_SETTINGS|
--<|

--:CLEAR_SETTINGS|
  --#title|ScriptCards
  --#titlecardbackground|#1C6EA4
  --#titlefontsize|1.2em
  --#titlefontlineheight|1.2em
  --#titlefontface|Helvetica
  --#titlecardgradient|0
  --#titlecardbackgroundimage|
  --#norollhighlight|0
  --#subtitleseparator| ♦
  --#hideTitleCard|0
  --#bodyfontsize|14px;
  --#bodyfontface|Helvetica
  --#oddrowbackground|#D0E4F5
  --#oddrowfontcolor|#000000
  --#evenrowbackground|#eeeeee
  --#evenrowfontcolor|#000000
  --#buttonbackground|#1C6EA4
  --#buttontextcolor|White
  --#buttonbordercolor|#999999
  --#buttonfontsize|x-small
  --#buttonfontface|Tahoma
  --#dicefontcolor|#1C6EA4
  --#dicefontsize|3.0em
  --#usehollowdice|0
  --#emotebackground|#f5f5ba
  --#emotestate|visible
  --#emotetext|
  --#tableborderradius|6px;
  --#tableshadow|5px 3px 3px 0px #aaa;
  --#debug|0
  --#whisper|
  --#bodybackgroundimage|
  --#evenrowbackgroundimage|
  --#oddrowbackgroundimage|
  --#buttonbackgroundimage|
  -->LOAD_CURRENT_SETTINGS|  
  --<|
}}