!script {{  
  --#title|Kill Selected token and remove from order
  --#hideCard|1
  --#whisper|self
  --#emoteState|1
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#debug|0
  --~|turnorder;removetoken;@{selected|token_id}
  --@token-mod|_ids @{selected|token_id} _set statusmarkers|dead bar1_value|0
}}