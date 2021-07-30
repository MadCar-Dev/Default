!script {{  
  --#title|Round Counter
  --#titleCardBackground|#932729
  --#hideCard|1
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#debug|0
 
  --@act| +1 1 _>>>Round<<<
  }}

  !script {{  
  --#title|Add Turnorder Counter
  --#titleCardBackground|#932729
  --#hideCard|1
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#debug|0
 
  --@act| ?{Direction|Count Down,-1|Count Up,+1} ?{Starting Position|} _?{Name|} 
}}

!script {{  
  --#title|Add Custom Item
  --#titleCardBackground|#932729
  --#hideCard|1
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#debug|0
  --~|turnorder;addcustom;?{Turnorder Item?|};?{Turnorder Value?|}
  }}

  !script {{  
  --#title|Remove Token from Token Order
  --#titleCardBackground|#932729
  --#sourcetoken|@{selected|token_id}
  --#hideCard|1
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#debug|0
  --~|turnorder;removetoken;@{selected|token_id}
}}