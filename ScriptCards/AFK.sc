!script {{

  --/|Script Name : Away From Keyboard (AFK)
  --/|Version     : 0.1
  --/|Requires SC : 1.3.7+
  --/|Author      : Will M.

  --/|Description : Quick AFK Toggle.  Assumes you have an AFK Status Marker
  --/|

  --:TOP|
  --#sourceToken|@{selected|token_id}
  --#leftsub|@{selected|character_name}
  --#hidecard|0
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#whisper|gm
  --#titleCardBackground|[&SendingPlayerColor]
  --#debug|1

  --&AFK|AFK

  --/|Loop through all of the status markers on the targetted token 
  --~|array;statusmarkers;aryToken_SM;@{selected|token_id}
  --~SMItem|array;getfirst;aryToken_SM

    --:LOOPTOP|
    --?[&SMItem] -eq ArrayError|ENDLOOP

      --&SMItem_Name|[&SMItem]
      --/| The rbutton syntax doesn't like '::' in the name- messes with the parser
      --~SMItem|string;replace;::;^^;[&SMItem]
      --~SMItem_Name|string;before;::;[&SMItem_Name]
      --?[&AFK] -eq [&SMItem_Name]|AFK_ON
      --~SMItem|array;getnext;aryToken_SM
    --^LOOPTOP|
    --:ENDLOOP| Not AFK
      --#title|AFK
    	--@token-mod|_set statusmarkers|[&AFK] _ids @{selected|token_id} _ignore-selected
    	--/|+|[c]@{selected|character_name} is now [b]"Away From Keyboard"[/b][/c]
    	--X|
    --:AFK_ON|
      --#title|Back
    	--@token-mod|_set statusmarkers|-[&AFK] _ids @{selected|token_id} _ignore-selected
    	--/|+|[c]@{selected|character_name} is [b]back[/b][/c]    	
			--X|

}}