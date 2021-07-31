!script {{  
  --#title|Potions & Scrolls
  --#reentrant|Potions
  --:TOP|

  --&FColor1|#66FF66 
  --&FColor1|#f0fff8
  --&FColor2|#00FF66
  --&BColor1|#282828
  --&BColor2|#282828

  --#titlecardbackgroundimage| linear-gradient(to bottom, #000000, [&BColor1])   
  --#titlefontsize|1.0em
  --#titlefontlineheight|1.2em
  --#titlefontface|Courier
  --#norollhighlight| 0
  --#hideTitleCard| 0
  --#bodyfontsize| 10px
  --#bodyfontface|Courier
  --#oddrowbackground|[&BColor1]
  --#oddrowfontcolor|[&FColor2]
  --#evenrowbackground|[&BColor1]
  --#evenrowfontcolor|[&FColor1]
  --#buttonbackground|[&BColor1]
  --#buttontextcolor|[&FColor1]
  --#buttonbordercolor|[&BColor1]
  --#buttonfontsize|x-small
  --#buttonfontface|Courier
  --#whisper|self
  --#debug|1

  --+GM|[&SendingPlayerIsGM]
  --+ID|[&SendingPlayerID]
  --+Name|[&SendingPlayerName]
  --+As|[&SendingPlayerSpeakingAs]
  --+Color|[&SendingPlayerColor]


  --&tStyle|style="width:100%;text-align:left;padding:0px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px [&BColor1];border: 0px dashed [&BColor1];"
  --&trStyle1|style="border:0px dashed [&FColor2];"
  --&trStyle2|style="border:0px dashed [&BColor1];"
  --&tdStyle1|style="width:5%;text-align:left;background-color:[&BColor1];font-size:100%"
  --&tdStyle2|style="width:65%;text-align:left;background-color:[&BColor1];font-size:100%"
  --&tdStyle3|style="width:30%;text-align:right;background-color:[&BColor1];font-size:100%"
  
  --&t|
  --=PCntr|1

  --/|Load list of player characterIds into gCharIds variable
  -->LOAD_CHARACTERIDS| 

  --~|array;fromstring;aryCharIds;,;[&gCharIds]
  --~CharId|array;getfirst;aryCharIds
	--~CharId|string;trim;[&CharId]
  --:CHAR_LOOP_START| Loop through all the items in the list
  --?[&CharId] -eq ArrayError|DONE

		--&[&EditRights]|0
		--?[&SendingPlayerIsGM] -eq 1|&EditRights;1
		--?"[*[&CharId]:controlledby]" -inc "[&SendingPlayerID]"|&EditRights;1
		--?"[*[&CharId]:controlledby]" -inc "all"|&EditRights;1

		--+EditRights|[&EditRights]

	  --Rfirst|[&CharId];repeating_inventory
	  --:INV_LOOP_TOP|

	    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|INV_LOOP_END
	    --~ITEM|string;touppercase;[*R:itemname]

	    --?"[&ITEM]" -inc "POTION"|ADD_ITEM
	    --?"[&ITEM]" -inc "OIL"|ADD_ITEM
	    --?"[&ITEM]" -inc "DUST"|ADD_ITEM
	    --?"[&ITEM]" -inc "SCROLL"|ADD_ITEM	    	    

	    --^SKIP_ITEM|
	    --:ADD_ITEM|

	    	--&IC|[*R:itemcount]
	    	--?X[&IC] -eq X|&IC;1

	    	-->ROLL20_ITEM_LINK|[*R:itemname]
				--=PCntr|[$PCntr] + 1	          	

	      --&t|+ [tr [&trStyle1]]
		           	[td [&tdStyle1]][&IC][/td]
		            [td [&tdStyle2]][button][*R:itemname]::[&zROLL20_MagicItem_Link][/button][/td]


		    --?[&EditRights] -eq 1|ADD_COMMANDS
			    --&t|+ [td [&tdStyle3]]XXX[/td][/tr]
			    --^SKIP_ITEM|
		    --:ADD_COMMANDS|
			    --&t|+ [td [&tdStyle3]][rbutton]+::INC_ITEMCOUNT;[&CharId]\[*R>itemcount][/rbutton]
			           								  [rbutton]-::DEC_ITEMCOUNT;[&CharId]\[*R>itemcount][/rbutton]
			           								  [rbutton]->::MOVE_ITEM;[&CharId]\[*R>itemcount][/rbutton][/td][/tr]

	    --:SKIP_ITEM|

	    --Rnext|
	  --^INV_LOOP_TOP|Back to top of loop
	  --:INV_LOOP_END|

	  --/|?[$PCntr.Total] -eq 0|NEXT_CHAR
	  	--&CN|[*[&CharId]:character_name]  -->SHORT_NAME|[&CN]

	    --?[&EditRights] -eq 1|ADD_NEWITEM_BUTTON
				--&Hdr|"[&gSN]"
				--^SKIP_NEWITEM_BUTTON|
	    --:ADD_NEWITEM_BUTTON|
		  	--&Hdr|"[&gSN]&nbsp;&nbsp;[rbutton]+::ADD_NEW_ITEM;[&CharId][/rbutton]"
			--:SKIP_NEWITEM_BUTTON|

	  	-->SECTION_HEADER|[&Hdr]
	  	
	  	--+Controlled By|[*[&CharId]:controlledby]

		  --&t|[t [&tStyle]] [&t] [/t]
		  --+|[&t]

		--:NEXT_CHAR|

  	--~CharId|array;getnext;aryCharIds
		--&t|
		--=PCntr|0
  	--^CHAR_LOOP_START|
  --:DONE|

  -->FOOTER_BUTTONS_MAIN|
  --X|

--:ADD_NEW_ITEM|
	--#Title|Add New Item
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]

  	--&CName|[*[&CharId]:character_name]
  	--I;Add new Potion or Scroll for [&CName]|q;ItemName;New Potion/Scroll?

  	--@setattr|_charid [&CharId] _silent _repeating_inventory_-CREATE_itemname|[&ItemName] _repeating_inventory_-CREATE_itemcount|1 _repeating_inventory_-CREATE_equipped|1 _repeating_inventory_-CREATE_useasresource|1 

		--&LogMsg|[*[&CharId]:character_name] added a [&ItemName] to their inventory
		-->LOG|"[&LogMsg]"
		--+|[&LogMsg]
		-->FOOTER_BUTTONS_MAIN|
	--x|
--<

--:INC_ITEMCOUNT|
	--#Title|Increase Item Count
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&ItemCountField|[&Arg2]
  --@setattr|_charid [&CharId] _silent _mod _[&ItemCountField]|1

	--~RFName|string;before;_itemcount;[&ItemCountField]
	--&ItemNameField|[&RFName]_itemname
	--&ItemName|[*[&CharId]:[&ItemNameField]]
	--=ItemCount|[*[&CharId]:[&ItemCountField]] + 1
  --&LogMsg|[*[&CharId]:character_name] increased inventory of [&ItemName] to [$ItemCount.Raw]
	-->LOG|"[&LogMsg]"
	--+|[&LogMsg]
	-->FOOTER_BUTTONS_MAIN|
  
  --/|^TOP|
  --x|
--<|

--:DEC_ITEMCOUNT|
	--#Title|Reduce Item Count
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&ItemCountField|[&Arg2]
  --=ItemCount|[*[&CharId]:[&ItemCountField]]

  --?[$ItemCount.Raw] -eq 1|DELETE_ITEM
  	--@setattr|_charid [&CharId]  _silent _mod _[&ItemCountField]|-1
		
		--~RFName|string;before;_itemcount;[&ItemCountField]
		--&ItemNameField|[&RFName]_itemname
		--&ItemName|[*[&CharId]:[&ItemNameField]]
		--=ItemCount|[$ItemCount.Total] - 1
		--&LogMsg|[*[&CharId]:character_name] decreased inventory of [&ItemName] to [$ItemCount.Raw]
		-->LOG|"[&LogMsg]"
		--+|[&LogMsg]
		-->FOOTER_BUTTONS_MAIN|

  	--x|

  --:DELETE_ITEM|

  	--~RFName|string;before;_itemcount;[&ItemCountField]
  	--/|*RFName|[&RFName]
		--&ItemNameField|[&RFName]_itemname
		--&ItemName|[*[&CharId]:[&ItemNameField]]

  	--@delattr|_charid [&CharId]  _silent _[&RFName]

		--&LogMsg|[*[&CharId]:character_name] used the last of [&ItemName]
		-->LOG|"[&LogMsg]"
		--+|[&LogMsg]
		-->FOOTER_BUTTONS_MAIN|

  --/|^TOP|
  --x|
--<|

--:MOVE_ITEM|[From_Char], [FC_Item], [FC_ItemName]
	--#Title|Move Item to Other Player

  --~Arg|string;split;\;[&reentryval]
  --&From_CharId|[&Arg1]
  --&ItemNameField|[&Arg2]

	--~RFName|string;before;_itemcount;[&ItemNameField]
	--&ItemNameField|[&RFName]_itemname
	--&ItemName|[*[&From_CharId]:[&ItemNameField]]

  --I;Give "[&ItemName]" to a friend?|t;To_TokenId;Give [&ItemName] to?
  --/| Verify this is a valid Party member

  --?"[*[&To_TokenId]:playercharacter]" -eq 1|MI_PC
  --?"[*[&To_TokenId]:t-represents]" -ninc "-"|MI_SKIP
  --?"[*[&To_TokenId]:npc]" -eq 1|MI_SKIP
  --?"[&To_TokenId]" -eq "[&From_CharId]"|MI_SKIP

  --:MI_PC|
  --&To_CharId|[*[&To_TokenId]:t-represents]

  --/|Add Item to the To Char Inventory

  	--/| Check to see if it already exists, and just add it to their inventory if it doesn't
	  --Rfirst|[&To_CharId];repeating_inventory
	  --~New_Item|string;touppercase;[&ItemName]
	  --~New_Item|string;trim;[&New_Item]
	  --:MI_INV_LOOP_TOP|

	    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|MI_INV_LOOP_NOT_FOUND
	    --~To_Item|string;touppercase;[*R:itemname]
	  	--~To_Item|string;trim;[&To_Item]

	    --/|*Compare|[&To_Item] -ne [&New_Item]
	    --?"[&To_Item]" -ne "[&New_Item]"|MI_NEXT_ITEM
	    	  --@setattr|_charid [&To_CharId] _silent _mod _[*R>itemcount]|1
	    	  --^MI_INV_LOOP_END|
	    --:MI_NEXT_ITEM|
		    --Rnext|
		    --^MI_INV_LOOP_TOP|

  	--:MI_INV_LOOP_NOT_FOUND|
  		--/| Didn't Find if we got here, so add it as a whole new item
  		--@setattr|_charid [&To_CharId] _silent _repeating_inventory_-CREATE_itemname|[&ItemName] _repeating_inventory_-CREATE_itemcount|1 _repeating_inventory_-CREATE_equipped|1 _repeating_inventory_-CREATE_useasresource|1 
  	
  	--:MI_INV_LOOP_END|

  --/|Remove Item from the From char inventory
	--&ItemCountField|[&RFName]_itemcount
  --=ItemCount|[*[&From_CharId]:[&ItemCountField]]
  --*From ItemCount| [$ItemCount.Raw]
  --?[$ItemCount.Raw] -eq 1|MI_DELETE_ITEM
  	--@setattr|_charid [&From_CharId]  _silent _mod _[&ItemCountField]|-1
  	--^MI_DONE|

  --:MI_DELETE_ITEM|
  	--@delattr|_charid [&From_CharId] _silent _[&RFName]
  	--^MI_DONE|

  --:MI_DONE|

	--&LogMsg|[*[&From_CharId]:character_name] handed [*[&To_CharId]:character_name] a [&ItemName]
	-->LOG|"[&LogMsg]"
	--+|[&LogMsg]
	-->FOOTER_BUTTONS_MAIN|
	--x|

	--:MI_SKIP|
		--+ERROR|Not a valid Player Character
		-->FOOTER_BUTTONS_MAIN|		
	--X|

--<|

--:LOAD_CHARACTERIDS|
  --/|Campaign SheetId
  --&CSId|-MY16YzwbcQ-rc18_HjI
  --&gCharIds|[*[&CSId]:characters]
--<|

--:SHORT_NAME|Shortens player name for reporting purposes (6 characters); Parameter:Character_Name
  --~gSN|string;left;8;[%1%]
  --~gSN|string;before; ;[&gSN]
--<| Return

--:FIND_TOKEN|TokenId
  --#hidecard|1  
  --@ping-token|_[&reentryval] _[&SendingPlayerID]
  --X|
--<|

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px [&BColor1];border:1px solid [&FColor2];"
  --&hdrstyle_TR|style="border:0px solid #FFFFFF;"
  --&hdrstyle_TD|style="width:100%;background-color:[&FColor2]; color:#000000; font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][c][%1%][%2%][/c][/td][/tr][/t]
--<|

--:FOOTER_BUTTONS_MAIN|
    --+|[l][rbutton]Refresh::TOP[/rbutton][/l]
            [r][button]MapNotes::~Mule|MapNote-Tools[/button]
            [button]NPC::~Mule|NPC-Tools[/button]
            [button]DM::~Mule|DM-Tools[/button]
            [/r]
--<|

--:LOG|Text to log
    --~DT|system;date;getdatetime
    --@note-log|[&DT]: [%1%]
--<|

--:DNDBEYOND_MAGICITEM_LINK|Magic Item 
  --/|Format is https://www.dndbeyond.com/magic-items/potion-of-healing
  --~MN|string;replaceall; ;-;[%1%]
  --&zDnDBeyond_MagicItem_Link|https://www.dndbeyond.com/magic-items/[&MN]
--<|

--:ROLL20_ITEM_LINK|ITEM NAME
  --/|Format is https://app.roll20.net/compendium/dnd5e/item name
  --/| ~MN|string;replaceall; ;-;[%1%]
  --&zROLL20_MagicItem_Link|https://app.roll20.net/compendium/dnd5e/[%1%]
--<|

}}
