!scriptcard {{ 

  --/|Script Name : NPC Layers
  --/|Version     : 4.1
  --/|Requires SC : 1.4.0+, TokenMod, ping-token
  --/|Author      : Will M.

  --/|Description : Lists NPCs and lets you quickly toggle them between layers

  --/|Updates     : 4.1 - Added pagetokens prefilter (NPC) to speed up code; 
  --/|                    Cleaned up formatting

  --:TOP|
  --#reentrant|NPCLayerTool  
  --#title|NPC Layer Tool
  --#titleCardBackground|#095c1f
  --#titlefontface|Patrick Hand
  --#oddRowBackground|#BADFBB
  --#evenRowBackground|#BADFBB
  --#buttonbackground|#BADFBB
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#BADFBB
  --#hidecard|0    
  --#bodyFontSize|11px
  --#whisper|gm
  --#debug|0
  --Ssettings|NPCLayers

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id};npc
  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens
	--?[&TokenId] -eq ArrayError|ENDLOOP

  --?"[*[&TokenId]:playercharacter]" -eq 1|CONTINUE

  --:LOOPCHECK|
    --/+Debug|[*[&TokenId]:t-name], [*[&TokenId]:t-layer], [*[&TokenId]:npc], [*[&TokenId]:t-represents]

    --/|Skip targets that are not NPCs
    --/|?[*[&TokenId]:t-layer] -ne objects|CONTINUE
    --/|?"[*[&TokenId]:npc]" -ne 1|CONTINUE
    --/|?"[*[&TokenId]:t-represents]" -inc "-"|START
    --/|^CONTINUE|

   --:START|

   	--&ObjMark|o
   	--&GMMark|g
   	--&MapMark|m
   	--&WallMark|l

		--C[*[&TokenId]:t-layer]|objects:OBJECTS|gmlayer:GM|map:MAP|walls:WALLS

		--:OBJECTS| --&ObjMark|[b][u]O[/u][/b] --^BUILDROW|
		--:GM| --&GMMark|[b][u]G[/u][/b] --^BUILDROW|
		--:MAP| --&MapMark|[b][u]M[/u][/b] --^BUILDROW|
		--:WALLS| --&WallMark|[b][u]L[/u][/b] --^BUILDROW|

		--:BUILDROW|

    --&tbl|+ [tr][td style="width:68%;text-align:left;background-color:#BADFBB;font-size:80%"] 
    						[b][img width=30 height=30][*[&TokenId]:t-imgsrc][/img][rbutton][*[&TokenId]:t-name]::FIND_TOKEN;[&TokenId][/rbutton][/b][/td]
        				[td style="width:8%;text-align:center;background-color:#BADFBB;font-size:80%"][rbutton][&ObjMark]::SET_LAYER;[&TokenId]\objects[/rbutton][/td]
        				[td style="width:8%;text-align:center;background-color:#BADFBB;font-size:80%"][rbutton][&GMMark]::SET_LAYER;[&TokenId]\gmlayer[/rbutton][/td]
        				[td style="width:8%;text-align:center;background-color:#BADFBB;font-size:80%"][rbutton][&MapMark]::SET_LAYER;[&TokenId]\map[/rbutton][/td]
        				[td style="width:8%;text-align:center;background-color:#BADFBB;font-size:80%"][rbutton][&WallMark]::SET_LAYER;[&TokenId]\walls[/rbutton][/td]
        	   [/tr]
        
    --:CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|LOOPCHECK
  --:ENDLOOP|

  --&tStyle|"width:100%;text-align:left;padding:1px;border-spacing:1px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px dashed black;"
  --&tbl|[t style=[&tStyle]] [&tbl] [/t]
  --+|[&tbl]
  -->FOOTER_BUTTONS|
--X|

--:FIND_TOKEN|TokenName
  --#hidecard|1  
  --/@find-token|_[&reentryval]
  --@ping-token|_[&reentryval]
  --X|
--<|

--:SET_LAYER|TokenName, layer
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --+Debug|[&reentryval]
  --+Debug|[&Arg1] [&Arg2]
  --&TokenId|[&Arg1]
  --&Layer|[&Arg2]
  --@token-mod|_set layer|[&Layer] _ids [&TokenId] _ignore-selected
  --X|
--<|

--:FOOTER_BUTTONS|
    --+|[r][button]NPC::~Mule|NPC-Tools[/button][button]DM::~Mule|DM-Tools[/button][button]&#x1F504;::~Mule|NPC-Layers[/button][/r]  
--<|
}}
