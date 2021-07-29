on('ready', () => {

    const getGM = (gmid) => {
      if (gmid === 'API'){
          log('Player is API');
        findObjs({type:'player'})
        .forEach(p=>{
            log('PlayerInfo: ' + p.id + "," + p.get("_type") + "," + p.get("_d20userid") + "," + p.get("_displayname"));
            if(playerIsGM(p.id)){
              gmid = p.id;
            }});
      } else {
          log('Player is gm');
      }
      return gmid;
    }

    on('chat:message', (msg) => {
        if ('api' === msg.type && /^!ping-token\b/i.test(msg.content)) {
            
            // Used by SelectManager to preserve selected token when nesting APIs
            if('API' === msg.playerid) msg.selected = getSelected();
            
            log('msg.content:' + msg.content);
            log('msg.playerid:' + msg.playerid);
            const gm = playerIsGM(msg.playerid);

            // Returns GM_ID if msg.playerid is API
            const gmid = getGM(msg.playerid);
            log('gm/gmid:' + gm + '/' + gmid);
            
            // const pname = getObj('player', msg.playerid).get('_displayname');
            const args = msg.content.split("--");
            log('args:' + args);

            const token_id = (args[1] || '').trim();
            const player_id = (args[2] || '').trim();
            
            log('token_id:' + token_id);
            log('player_id:' + player_id);
            
            if (token_id) {
                const t = getObj('graphic',token_id);
                
                if (t) {

                    if (player_id){
                        log('Sending Ping! to Player' + t.get("left")+ ", " + t.get("top") + ", " + t.get("_pageid") + ", " + player_id);
                        sendPing(t.get("left"), t.get("top"),  t.get("_pageid"), player_id, true, player_id);

                        const p = getObj('player', player_id);
                        sendChat('PingToken', `/w gm Player <code>${p.get("_displayname")}</code> pinging token <code>${t.get("name")}</code>.`);

                    } else {
                        log('Sending Ping to GM' + t.get("left")+ ", " + t.get("top") + ", " + t.get("_pageid") + ", " + gmid);
                        sendPing(t.get("left"), t.get("top"),  t.get("_pageid"), gmid, true, gmid);
                    }

                } else {
                    sendChat('PingToken', `/w gm No tokens with token_id <code>${token_id}</code> and owned by you found on your current page.`);
                }
            } else {
                sendChat('PingToken', `/w gm Please use the format <code>!ping-token --token_id</code>.`);
            }
        }
    });
    log("-=> Ping Token Loaded (!ping-token) [Last Edited May 17th 2021] <=-");
});
