var API_Meta = API_Meta || {};
API_Meta.Stealth = { offset: Number.MAX_SAFE_INTEGER, lineCount: -1 };
{
  try { throw new Error(''); } catch (e) { API_Meta.Stealth.offset = (parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/, '$1'), 10) - (4)); }
}


on('ready',()=>{
  log('>>>-----> Stealth is ready! --offset '+ API_Meta.Stealth.offset);
	on('chat:message',msg=>{
		if('api'===msg.type && /^!sb3n(\b\s|$)/i.test(msg.content)){
            let who = (getObj('player',msg.playerid)||{get:()=>'API'}).get('_displayname');
            let notes = [];
            (msg.selected || [])
                .map(o=>getObj('graphic',o._id))
                .filter(g=>undefined !== g)
                .map(o=>({token: o, character: getObj('character',o.get('represents'))}))
                .filter(o=>undefined !== o.character)
                .forEach(o=>{

                  let sm = o.token.get("statusmarkers")
                  log(`Stealth: StatusMarkers: ${sm}`)

                  let smTrickster = "Blessing-Trickster::6089836";
                  let smStealth = "Stealth::4082914";
                  let sbroll = 0
                  let sbroll1 = 0
                  let sbroll2 = 0
                  let tt = '';
                  if(sm.includes(smTrickster)){
                    sbroll1 = randomInteger(20);
                    sbroll2 = randomInteger(20);
                    sbroll = Math.max(sbroll1,sbroll2);

                    if (sbroll1 == sbroll){
                      sbroll1 = `**${sbroll1}**`
                    } else {
                      sbroll2 = `**${sbroll2}**`
                    }

                    tt = `(${sbroll1}/${sbroll2})[Blessing of the Trickster]`
                    log(`Stealth: ${tt}`)
                  } else {
                    sbroll = randomInteger(20);
                    tt = `**${sbroll}**`
                  }

                  let sbflag = (parseInt(getAttrByName(o.character.id,'npc_stealth_flag'))||0);
                  let sb = 0;
                  if(sbflag > 0) {
                  	sb = parseInt(getAttrByName(o.character.id,'npc_stealth'))||0;
                  } else {
              		sb = parseInt(getAttrByName(o.character.id,'stealth_bonus'))||0;
              	  }
                  let sbresult = sbroll + sb;

                  if (!sm.includes(smStealth)) {
                    sm = `${smStealth},${sm}` 
                  }

                  o.token.set({
                    /* showname: true, */
                    /* showplayers_name: true, */
                    /* bar3_value: sbresult, */
                    tooltip: `Stealth DC${sbresult} = ${tt}+${sb}[bonus]`, 
                    show_tooltip: true,
                    statusmarkers: sm
                  });
                  notes.push(`${o.character.get('name')} Stealth roll: **${sbresult}** = ${tt} + **${sb}**[bonus]`);
                });

            sendChat('',`/w "${who}" <div><ul>${notes.map(n=>`<li>${n}</li>`).join('')}</ul></div>`);
            if(!playerIsGM(msg.playerid)){
                sendChat('',`/w GM <div><ul>${notes.map(n=>`<li>${n}</li>`).join('')}</ul></div>`);
            }
        
            
		}
	});
});
{try{throw new Error('');}catch(e){API_Meta.Stealth.lineCount=(parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/,'$1'),10)-API_Meta.Stealth.offset);}}