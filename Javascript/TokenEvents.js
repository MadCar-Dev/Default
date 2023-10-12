var API_Meta = API_Meta || {};
API_Meta.myTokenEvents = { offset: Number.MAX_SAFE_INTEGER, lineCount: -1 };
{
  try { throw new Error(''); } catch (e) { API_Meta.myTokenEvents.offset = (parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/, '$1'), 10) - (4)); }
}

// This script does a number of automations 
//   * Updates the Health Status Markers for all tokens, responding to graphic change events
//   * Enables users to select tokens to follow other tokens
//   * Tracks movement history. (I hope to add replay logic at some point)
//   * Generates a master DMTools dialog that encorporates DMDash, Campaign Health and options within this script

// Future enhancement 
// * Replay moveData (Back a number of events; between 2 ids; All Tokens or just a select token)
//   * Limit to only the current page and make sure to End at the Last Position.  
//   * Need to capture Current Position of every token for the last move.  
//   * Will need to be able to slow down movement
// * Add Patrol features


on('ready', () => {
  const version = '0.0.05'; // Patrol version (not yet ready)
  let gDebug = 0
  let gDebugLvl  = 4
  
  log('>>>-----> MyTokenMovements ' + version + ' is ready! --offset '+ API_Meta.myTokenEvents.offset);

  // state.myTokenEvents = '';

  if(!state.myTokenEvents) {
    state.myTokenEvents = {
        version: version,
        followData: [],
        moveData: [],
        patrolData: []
    };
  }

  if (!state.myTokenEvents.hasOwnProperty('followData') || state.myTokenEvents.followData === undefined) {  
    state.myTokenEvents.followData = [];
  }

  if (!state.myTokenEvents.hasOwnProperty('moveData') || state.myTokenEvents.moveData === undefined) {  
    state.myTokenEvents.moveData = [];
  }

  if (!state.myTokenEvents.hasOwnProperty('patrolData') || state.myTokenEvents.patrolData === undefined) {  
    state.myTokenEvents.patrolData = [];
  }

    
  

  let gMsg = [];

  on('change:campaign:playerpageid', async () => {
    // Reset Movement data
    resetMoves()
    lastPosition = []
    initPosition = []
  });

  on('change:graphic', function(obj, prev) {

    if (obj.get('bar1_value') !== prev['bar1_value'] || obj.get('bar1_max') !== prev['bar1_max']) {
      setHealthIndicator(obj)
      checkConcentration(obj)
    }

    if (obj.get('left') === prev['left'] && obj.get('top') === prev['top']) return;

    let followers = getFollowers(obj.get('_id'));
    followers.forEach(f => {
      moveFollower(obj.get('_id'), f.sTokenid)
    });

    let master = getMaster(obj.get('_id'));
    if (master !== undefined) {
      moveMaster(master.mTokenid, obj.get('_id'))
    }

    if (state.myTokenEvents.moveData === undefined) {
      log('MoveData not defined!!!')
      state.myTokenEvents.moveData = [];
    } else {

      // log(`change:graphic:left: PrevTokenId: ${prevMovementTokenid} prevMovement: ${prevMovement}`)
      // if (prevMovementTokenid != obj.get('_id') || prevMovement != obj.get('lastmove')) {
        let mrId = Number(state.myTokenEvents.moveData.length) + 1
        let moveRec = {'id': mrId, 'pageid': obj.get('_pageid'), 'tokenid': obj.get('_id'), 'lastmove':obj.get('lastmove') }
        state.myTokenEvents.moveData.push(moveRec)
        // prevMovement = obj.get('lastmove')
        // prevMovementTokenid = obj.get('_id')
      // }
    }

  });

  on('chat:message', async (msg_orig) => {
    let msg = _.clone(msg_orig);
    if (!/^!Follow/i.test(msg.content) && 
        !/^!HealthCheck/i.test(msg.content) && 
        !/^!DMTools/i.test(msg.content) && 
        !/^!Replay/i.test(msg.content) && 
        !/^!Kill/i.test(msg.content) && 
        !/^!UnKill/i.test(msg.content) && 
        !/^!Patrol/i.test(msg.content) && 
        !/^!TokenInfo/i.test(msg.content)) {
      return;
    }
    gMsg = _.clone(msg_orig);
    log(`on chat:message: who:${gMsg.who}, playerid:${gMsg.playerid}, type:${gMsg.type}, Selected: ${gMsg.selected}` )
    handleMsg(msg);
    //debounced_HandleMsg(msg);

  })

//  const debounced_handleMsg = _.debounce(handleMsg(msg),100)

  const smH1 = 'HealthLvl1::4960092';
  const smH2 = 'Healthlvl2::4960093'
  const smH3 = 'HealthLvl3::4960094'
  const smH4 = 'HealthLvl4::4960095'
  const smDead = 'dead'
  let hpPct = 0;

  function handleMsg(msg){

    let args = msg.content.split(/\s--/);
    let commands = [];
    if (args.length > 1) {
      commands = args[1].match(/(?:[^\s"']+|"[^"]*")+/g);
      commands = commands.map(item => item.replace(/^"|"+$/g, ''));
    } else {

    }
  
    let selected = msg.selected
    if (selected === undefined || selected.length == 0){
      // log(`MsgHandler: no token selected`)
    } else {
      // log(`MsgHandler: selected: ${selected[0]._id}`)
    }

    if (commands.length > 0) {
      commands[0] = commands[0].toUpperCase();
    }
    let masterCmd = args[0].toUpperCase()
  
    if (masterCmd =='!FOLLOW') {
      switch (commands[0]) {
        case 'ADD':
        case 'EDIT':
            let leftOffset = 35;
            let topOffset = 35;
            if (commands[3] !== undefined) leftOffset = commands[3];
            if (commands[4] !== undefined) topOffset = commands[4];
            addFollower(commands[1], commands[2], leftOffset, topOffset)
            dumpFollowData()
          break;
        case 'DELETE':
          delFollower(commands[1], commands[2])
          break;
        case 'SHOW':
        case 'SHOWFOLLOWERS':
          showFollowers();
          dumpFollowData();
          break;
        case 'DUMPDATA':
          dumpFollowData();
          break;
        case 'CLEAR':
          state.myTokenEvents.followData = [];
          break;

        case 'SHOWMOVES':
        case 'SHOWMOVEMENTS':
          showTokenMoves();
          break;
        case 'CLEARMOVES':
        case 'CLEARMOVEMENTS':  
          clearMoves()
          break;
        case 'DIALOG':
        default:
          if (selected !== undefined) {
            buildUserDialog(selected[0]._id)
          }
          break;
      }
    } else if (masterCmd == '!HEALTHCHECK') {
      setHealthIndicatorsForPage();
    } else if (masterCmd == '!KILL') {
      if (selected !== undefined) {
        selected.forEach(tkn => {
          killToken(tkn._id)  
        })
      }
    } else if (masterCmd == '!UNKILL') {
          unKill();
    } else if (masterCmd == '!TOKENINFO') {
      if (selected !== undefined) {
        dumpToken(selected[0]._id)
      } else if (commands[0] !== undefined) {
        dumpToken(commands[0])
      }
    } else if (masterCmd == '!DMTOOLS') {
      buildDMToolsDialog();
    } else if (masterCmd == '!PATROL') {
      switch (commands[0]) {
        case 'ADDWAYPOINT':
          if (selected !== undefined) {
            selected.forEach(tkn => {
              patrol_AddWayPoint(tkn._id)  
            })
          }
          break;
        case 'RESET': //Only resets tokens on the selected page
          patrol_Reset();
          break;
        case 'NEXT':
          patrol_Next(1);          
          break;
        case 'PREV':
          patrol_Next(-1);          
          break;
        case 'REPORT':
          patrol_Report();
          break;
        case 'CLR-TOKEN-WPS':
          if (selected !== undefined) {
            selected.forEach(tkn => {
              patrol_ClearTokenWPs(tkn._id)
            });
          }
          break;
        case 'CLR-PAGE-WPS':
          patrol_ClearPageWPs();
          break;
        case 'CLR-ALL-WPS':
          patrol_ClearAllWPs();
          break;
        case 'SET-MODE': // Circular, Once, Patrol (back/forth)
          patrol_SetMode(commands[1], commands[2]);
          break;
        case 'DIALOG':
        default:
          patrol_Dialog();
      }

    } else if (masterCmd == '!REPLAY') {
      switch (commands[0]) {
        case 'NEXT':
          gMoveId = nextMove(gMoveid);
        case 'PREV':
          gMoveid = prevMove(gMoveid);
        case 'REWIND':
          gMoveid = rewindMoves();
        case 'END':
          gMoveid = resetMoves();
  
        case 'SHOWMOVES':
        case 'SHOWMOVEMENTS':
          showTokenMoves();
          break;
        case 'CLEARMOVES':
        case 'CLEARMOVEMENTS':  
          clearMoves()
          break;
      }
    }
  }
  
  // Potential Concentration Features
  //  * Detect spell casting (Slots change negatively on Character)
  //  * Look into monitoring chat for Spells
  //  * Look into the concentration public script for ideas
  //  * Detect caster concentrating was hit (token hp bar1 changes negatively)
  //     * Notify the DM and Notify the player/caster
  //     * Allow for a roll from the notification
  //  * Remove concentration


  function patrol_AddWayPoint(tokenid){
    // Data Structure for Token WayPoints
      // TokenId
      // PageId
      // Mode (Once, Circular, Patrol (Default))
      // Waypoints: string of x,y coordinates, similar to the path field
      // CurrentWP: current wp position (starts at zeor)
      // Direction: Indicates the direction for patrol mode
      // Footsteps: audio file to play (Future)
    let t = getObj('graphic', tokenid)
    if (!t) return;

    let pageid = t.get('_pageid')
    let wpMode = 'Patrol'
    let wp = `${t.get('left')},${t.get('top')}`
    let bFound = false
    let new_patrolRec = ''
    let newPatrolData = [];

    // Attempt to find a Waypoint record for this Token/Pageid combination
    for (let i = 0; i<state.myTokenEvents.patrolData.length; i++) {
      let patrolRec = state.myTokenEvents.patrolData[i]

      if(patrolRec.tokenid === tokenid) {
        bFound = true
        wp = patrolRec.waypoints + ',' + wp
        new_patrolRec = {'tokenid': patrolRec.tokenid, 'pageid': patrolRec.pageid, 'mode': patrolRec.mode, 'waypoints': wp, 'currWP': patrolRec.currWP, 'direction': patrolRec.direction, 'footsteps': patrolRec.footsteps}
        newPatrolData.push(new_patrolRec)
      } else {
        newPatrolData.push(patrolRec)
      }
    };

    // if not found - Add new WP record
    if (!bFound) {
      new_patrolRec = {'tokenid': tokenid, 'pageid': pageid, 'mode': wpMode, 'waypoints': wp, 'currWP': 0, 'direction': 1, 'footsteps':''}
      newPatrolData.push(new_patrolRec)
    }
    state.myTokenEvents.patrolData = newPatrolData;
  }

  function patrol_Reset(tokenid) {
    // Reset all tokens to 1st patrol position on the current page
    let pageid = '';
    let newPatrolData = [];
    if (tokenid) {
      let t = getObj('graphic', tokenid)
      if (!t) return;
      pageid = t.get('_pageid')
    } else {
      pageid = Campaign().get('playerpageid');
    }

    for (let i = 0; i<state.myTokenEvents.patrolData.length; i++) {
      let patrolRec = state.myTokenEvents.patrolData[i]
      if(patrolRec.pageid === pageid) {
        let wps = patrolRec.waypoints.split(',')

        // Get the first waypoionts reset the wp counter
        let t = getObj('graphic', patrolRec.tokenid)
        if (t) {
          t.set('left', Number(wps[0]))
          t.set('top', Number(wps[1]))
        }
        let new_patrolRec = {'tokenid': patrolRec.tokenid, 'pageid': patrolRec.pageid, 'mode': patrolRec.mode, 'waypoints': patrolRec.waypoints, 'currWP': 0, 'direction': patrolRec.direction, 'footsteps': patrolRec.footsteps}
        newPatrolData.push(new_patrolRec)
      } else {
        newPatrolData.push(patrolRec)
      }
    };
    state.myTokenEvents.patrolData = newPatrolData;
  }

  function patrol_Next(moveDir) {
  // Move to the next position for all tokens on the current page
    // Reset all tokens to 1st patrol position on the current page

    if (!moveDir || moveDir === undefined){
      movDir = 1;
    }
    let pageid = Campaign().get('playerpageid');
    let currWP = 0;
    let direction = 1;
    let newPatrolData = [];

    for (let i = 0; i<state.myTokenEvents.patrolData.length; i++) {
      let patrolRec = state.myTokenEvents.patrolData[i]
      if(patrolRec.pageid === pageid) {

        // Get the associated token
        let t = getObj('graphic', patrolRec.tokenid)
        if (t) {

          let wps = patrolRec.waypoints.split(',')
          log(`wps.length: ${wps.length}`)

          // Are we at the end of the wps? 
          let ndx = (Number(patrolRec.currWP) + (Number(moveDir) * Number(patrolRec.direction))) * 2
          log(`ndx: ${ndx} currWP: ${patrolRec.currWP} moveDir: ${moveDir} direction: ${patrolRec.direction}`)
          if (ndx > (wps.length -1) || ndx < 0){

            //What mode are we in?
            if (patrolRec.mode === 'Patrol') {
              direction = -1 * patrolRec.direction // Toggle the direction
              currWP = patrolRec.currWP + direction;
            } else if (patrolRec.mode === 'Circular') {
              direction = patrolRec.direction
              currWP = 0; // Back to start
            } else {  // Assume we are stopping at the end --- Nothing to do
              direction = patrolRec.direction
              currWP = patrolRec.currWP
            }
            ndx = Number(currWP) * 2
            let left = Number(wps[ndx])
            let top = Number(wps[ndx + 1])
            log(`ndx: ${ndx} left: ${left} top: ${top}`)

            t.set('left', left)
            t.set('top', top)
          } else {
            let left = wps[ndx]
            let top = wps[ndx + 1]
            log(`ndx: ${ndx} left: ${left} top: ${top}`)
            t.set('left', left)
            t.set('top', top)
            direction = patrolRec.direction
            currWP = Number(ndx) / Number(2)
          }
        }
        let new_patrolRec = {'tokenid': patrolRec.tokenid, 'pageid': patrolRec.pageid, 'mode': patrolRec.mode, 'waypoints': patrolRec.waypoints, 'currWP': currWP, 'direction': direction, 'footsteps': patrolRec.footsteps}
        newPatrolData.push(new_patrolRec)
      } else {
        newPatrolData.push(patrolRec)
      }
    };
    state.myTokenEvents.patrolData = newPatrolData;

  }

  function patrol_Prev() {
    patrol_Next(-1);
  }

  function patrol_Report() {
    // List waypoints in a table with options to 
    //   * Clear all patrols/waypoints
    //   * Change patrol mode
    const openReport = "<div style='color: #000; border: 1px solid #000; background-color: #EFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 5px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
    const closeReport = '</div>';

    btnRefresh = makeMenuButton('Refresh', `!patrol --report`)
    btnClear = makeMenuButton('Clear All Waypoints', `!patrol --CLR-ALL-WPS`)

    let output = ''
    let tbl = ''

    tbl = html.tr(html.th('Page') + html.th('Token') + html.th('Mode') + html.th('WayPoints') + html.th('Current WP') + html.th('Direction') + html.th('Footsteps Track'))
     // moveRec = {'id', 'pageid', 'tokenid', 'lastmove'}
     for (let i = 0; i<state.myTokenEvents.patrolData.length; i++) {
      let patrolRec = state.myTokenEvents.patrolData[i]

      let page = getObj('page', patrolRec.pageid).get('name');
      let t = getObj('graphic', patrolRec.tokenid)
      if (t){
        // Add logic to allow for changing mode here:
        tbl += html.tr(html.td(page) + html.td(t.get('name')) + html.td(patrolRec.mode) + html.td(patrolRec.waypoints) + html.td(patrolRec.currWP) + html.td(patrolRec.direction) + html.td(patrolRec.footsteps) )
      } else {
        tbl += html.tr(html.td(page) + html.td('<b><i>Missing</b></i>') + html.td(patrolRec.mode) + html.td(patrolRec.waypoints) + html.td(patrolRec.currWP) + html.td(patrolRec.direction) + html.td(patrolRec.footsteps) )
      }
    }

    tbl = html.table(tbl)

    output += html.h3('Patrol Data')
    output += tbl

    output = openReport + btnRefresh + btnClear + output + closeReport
    addTextToHandout(output, 'Patrol Data', 0);
    return;
  }

  function patrol_Dialog() {

    let myMsg = ''
    let tbl = ''
    let msgHandouts = '';
    let api = ''
    let btn = ''

    myMsg= html.p('Commands to facilitate token patrols', {'font-size': '11px'})

    tbl = html.tr(html.th("Run") + html.th("Description"))

    api = `!patrol --ADDWAYPOINT`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Add Waypoint'))

    api = `!patrol --next`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Next'))

    api = `!patrol --prev`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Prev'))

    api = `!patrol --reset`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Reset'))

    api = `!patrol --report`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Refresh Report'))

    api = `!patrol --CLR-TOKEN-WPS`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Clear Token Patrols'))

    api = `!patrol --CLR-PAGE-WPS`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Clear Page Patrols'))

    api = `!patrol --CLR-ALL-WPS`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Clear All Patrols'))
    
    tbl = html.table(tbl)

    myMsg+=tbl

    msgbox({msg: myMsg, 
          title: 'Patrol Dialog',
          whisperto: `GM`,          
          headercss: { 'background-color': defaultThemeColor1 }})
    
  }

  function patrol_ClearTokenWPs(tokenid) {
    let newPatrolData = [];
    for (let i = 0; i<state.myTokenEvents.patrolData.length; i++) {
      let patrolRec = state.myTokenEvents.patrolData[i]
      if(patrolRec.tokenid !== tokenid) {
        newPatrolData.push(patrolRec)
      }
    };
    state.myTokenEvents.patrolData = newPatrolData;
  }

  function patrol_ClearPageWPs() {
    let newPatrolData = [];
    let pageid = Campaign().get('playerpageid');
    for (let i = 0; i<state.myTokenEvents.patrolData.length; i++) {
      let patrolRec = state.myTokenEvents.patrolData[i]
      if(patrolRec.pageid !== pageid) {
        newPatrolData.push(patrolRec)
      }
    };
    state.myTokenEvents.patrolData = newPatrolData;
  }

  function patrol_ClearAllWPs() {
    state.myTokenEvents.patrolData = [];
  }

  function patrol_SetMode(tokenid, mode) {
    let newPatrolData = [];
    for (let i = 0; i<state.myTokenEvents.patrolData.length; i++) {
      let patrolRec = state.myTokenEvents.patrolData[i]

      if(patrolRec.tokenid !== tokenid) {
        newPatrolData.push(patrolRec)
      } else {
        let new_patrolRec = {'tokenid': patrolRec.tokenid, 'pageid': patrolRec.pageid, 'mode': mode, 'waypoints': patrolRec.waypoints, 'currWP': patrolRec.currWP, 'direction': patrolRec.direction, 'footsteps': patrolRec.footsteps}
        newPatrolData.push(new_patrolRec);
      }
    };
    state.myTokenEvents.patrolData = newPatrolData;
  }

  function killToken(tokenid) {

    let t = getObj('graphic', tokenid)
    if (!t) return;
    
    let c = getCharacterByName('BloodSplatter')
    if (!c) return;

    //Create Bubble special effect
    spawnFx(t.get('left'), t.get('top'), 'burst-blood', t.get('_pageid'));

    //Create new token based on BloodSplatter NCP sheet and use random number to assign side.
    let side = 0
    c.get('_defaulttoken', function(defaultToken) {

      try {

        let baseObj = JSON.parse(defaultToken);

        baseObj.imgsrc = getCleanImgsrc(baseObj.imgsrc); 
        if (baseObj.hasOwnProperty('sides')) {
          let sidesArr=baseObj["sides"].split('|');
          if (sidesArr[0] !== ''){
            side = randomInteger(sidesArr.length) - 1;
          }
          if (side >=0 && side <= sidesArr.length -1) {
            sideImg = getCleanImgsrc(sidesArr[side]);
            baseObj.currentside = side;
            baseObj.imgsrc = sideImg
          }
        }

        let r = randomInteger(2)
        if (r === 1) {
          baseObj.fliph = true
        } else {
          baseObj.fliph = false
        }

        r = randomInteger(2)
        if (r === 1) {
          baseObj.flipv = true
        } else {
          baseObj.flipv = false
        }

        baseObj.name = 'BloodSplatter';
        baseObj.pageid = t.get('pageid');
        baseObj.left = t.get('left')
        baseObj.width = t.get('width')
        baseObj.top = t.get('top')
        baseObj.height = t.get('height')
        baseObj.layer = 'objects'
        baseObj.isdrawing = true
        baseObj.tooltip = `${t.get('name')} remains.`

        log(`Dumping Blood Splatter Base Object`);
        dumpObject(baseObj);

        bs = createObj('graphic', baseObj);

        if (!bs) {
          log(`Error creating blood splatter token`)
        } else {
          // Move token to GM Layer and place in top left cell (35,35)
          // log(`token ${bs.get('name')} created with id: ${bs.get('_id')}`)
          // dumpToken(bs.get('_id'));
          
          t.set('layer','gmlayer');
          t.set('bar3_value', 'dead')
          let lastPosition = `${t.get('left')},${t.get('top')}`
          t.set('bar3_max', lastPosition)
          t.set('left', 105);
          t.set('top', 105);

        }
      } catch (err) {
        log(`Kill Token Error: ${err.message}`)
      }
    });

  }

  function unKill() {
    // For the current page only, unkills all tokens that were previously killed

    let pageid = Campaign().get('playerpageid');
    // Delete all blood splatter tokens
    let tokens = findObjs({
      _type: 'graphic',
      _subtype: 'token',
      name: 'BloodSplatter',
      _pageid: pageid
    })

    tokens.forEach(t => {
      t.remove() 
    })

    // Find all dead Tokens
    tokens = findObjs({
      _type: 'graphic',
      _subtype: 'token',
      bar3_value: 'dead',
      _pageid: pageid
    })
  
    // When a token is killed, this routine places its X,Y properties in the bar3_max value
    tokens.forEach(t => {
      let coord = t.get('bar3_max').split(',')
      if (coord.length >= 2) {
          t.set('left', coord[0])
          t.set('top', coord[1])
          // spawnFx(coord[0], coord[1], 'bomb-holy', pageid);
      }
      t.set('bar1_value', t.get('bar1_max'))
      t.set('bar3_value', '')
      t.set('bar3_max', '')
      t.set('statusmarkers', '')
      t.set('layer', 'objects')
    })


  }

  const getCleanImgsrc = function (imgsrc) {
    let parts = imgsrc.match(/(.*\/images\/.*)(thumb|med|original|max)([^\?]*)(\?[^?]+)?$/);
        if(parts) {
            return parts[1]+'thumb'+parts[3]+(parts[4]?parts[4]:`?${Math.round(Math.random()*9999999)}`);
        }
    return;
  };

  function dumpToken(tId){

    let t = getObj('graphic', tId)
    if (!t){
      log(`Unable to find token for id: ${tId}`)
      return 
    }

    log(`_id: ${t.get('_id')}`)
    log(`_type: ${t.get('_type')}`)
    log(`_subtype: ${t.get('_subtype')}`)
    log(`_cardid: ${t.get('_cardid')}`)
    log(`_pageid: ${t.get('_pageid')}`)
    log(`imgsrc: ${t.get('imgsrc')}`)
    log(`represents: ${t.get('represents')}`)
    log(`left: ${t.get('left')}`)
    log(`top: ${t.get('top')}`)
    log(`width: ${t.get('width')}`)
    log(`height: ${t.get('height')}`)
    log(`rotation: ${t.get('rotation')}`)
    log(`layer: ${t.get('layer')}`)
    log(`isdrawing: ${t.get('isdrawing')}`)
    log(`flipv: ${t.get('flipv')}`)
    log(`fliph: ${t.get('fliph')}`)
    log(`name: ${t.get('name')}`)
    log(`gmnotes: ${t.get('gmnotes')}`)
    log(`tooltip: ${t.get('tooltip')}`)
    log(`show_tooltip: ${t.get('show_tooltip')}`)
    log(`controlledby: ${t.get('controlledby')}`)
    log(`bar1_link: ${t.get('bar1_link')}`)
    log(`bar2_link: ${t.get('bar2_link')}`)
    log(`bar3_link: ${t.get('bar3_link')}`)
    log(`bar1_value: ${t.get('bar1_value')}`)
    log(`bar2_value: ${t.get('bar2_value')}`)
    log(`bar3_value: ${t.get('bar3_value')}`)
    log(`bar1_max: ${t.get('bar1_max')}`)
    log(`bar2_max: ${t.get('bar2_max')}`)
    log(`bar3_max: ${t.get('bar3_max')}`)
    log(`bar_location: ${t.get('bar_location')}`)
    log(`compact_bar: ${t.get('compact_bar')}`)
    log(`aura1_radius: ${t.get('aura1_radius')}`)
    log(`aura2_radius: ${t.get('aura2_radius')}`)
    log(`aura1_color: ${t.get('aura1_color')}`)
    log(`aura2_color: ${t.get('aura2_color')}`)
    log(`aura1_square: ${t.get('aura1_square')}`)
    log(`aura2_square: ${t.get('aura2_square')}`)
    log(`tint_color: ${t.get('tint_color')}`)
    log(`statusmarkers: ${t.get('statusmarkers')}`)
    log(`showname: ${t.get('showname')}`)
    log(`showplayers_name: ${t.get('showplayers_name')}`)
    log(`showplayers_bar1: ${t.get('showplayers_bar1')}`)
    log(`showplayers_bar2: ${t.get('showplayers_bar2')}`)
    log(`showplayers_bar3: ${t.get('showplayers_bar3')}`)
    log(`showplayers_aura1: ${t.get('showplayers_aura1')}`)
    log(`showplayers_aura2: ${t.get('showplayers_aura2')}`)
    log(`playersedit_name: ${t.get('playersedit_name')}`)
    log(`playersedit_bar1: ${t.get('playersedit_bar1')}`)
    log(`playersedit_bar2: ${t.get('playersedit_bar2')}`)
    log(`playersedit_bar3: ${t.get('playersedit_bar3')}`)
    log(`playersedit_aura1: ${t.get('playersedit_aura1')}`)
    log(`playersedit_aura2: ${t.get('playersedit_aura2')}`)
    log(`lastmove: ${t.get('lastmove')}`)
    log(`sides: ${t.get('sides')}`)
    log(`currentSide: ${t.get('currentSide')}`)
    log(`lockMovement: ${t.get('lockMovement')}`)
  }
  function getCharacterByName(characterName) { // #### Not Used - Could be moved to my Library of functions ###
    // Find the character with the specified name
    let character = findObjs({
      _type: 'character',
      name: characterName,
    })[0];
  
    // Return the character object or null if not found
    return character || null;
    }  

  function checkConcentration(token) {
    // Calleed if the Bar1 Value Changes

    // Is the Concentration Marker on?

      // No - Nothing to do

      // Yes - Whisper to the DM to have player make a concentration check

      //  Ideally, allow me to forward a prompt with an option to roll

  }

  function showTokenMoves() {
    // List moves in a table with options to 
    //   * Play moves
    //   * Stop moves (when playing)
    //   * move next
    //   * move previous
    //   * clear moves
    //   * reset to current state
    const openReport = "<div style='color: #000; border: 1px solid #000; background-color: #EFEBD6; box-shadow: 0 0 3px #000; display: block; text-align: left; font-size: 13px; padding: 5px; margin-bottom: 2px; font-family: sans-serif; white-space: pre-wrap;'>";
    const closeReport = '</div>';

    btnRefresh = makeMenuButton('Refresh', `!follow --showmoves`)
    btnClear = makeMenuButton('Clear Movements', `!follow --clearmoves`)

    let output = ''
    let tbl = ''
    let pageid = Campaign().get('playerpageid');
    let page = getObj('page', pageid).get('name');
    
    tbl = html.tr(html.th('Id') + html.th('Token Name') + html.th('Last Move'))
     // moveRec = {'id', 'pageid', 'tokenid', 'lastmove'}
    state.myTokenEvents.moveData.forEach(moveRec=>{
      if (pageid === moveRec.pageid){
        let t = getObj('graphic', moveRec.tokenid)
        if (t){
          tbl += html.tr(html.td(moveRec.id) + html.td(t.get('name')) + html.td(moveRec.lastmove))
        } else {
          tbl += html.tr(html.td(moveRec.id) + html.td('<b><i>Missing</b></i>') + html.td(moveRec.lastmove))
        }
      }
    });

    tbl = html.table(tbl)
    output = html.h3('Move History for Page: ' + page)
    output += tbl
    tbl = '';

    // Now get current token positions
    let tokens = findObjs({
      _type: 'graphic',
      _subtype: 'token',
      // controlledby: '',
      _pageid: pageid
    }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));

    tbl = html.tr(html.th('TokenId') + html.th('Token Name') + html.th('Current Positon'))
    tokens.forEach(t=>{
      tbl += html.tr(html.td(t.get('_id')) +  html.td(t.get('name')) + html.td(`${t.get('left')}, ${t.get('top')}`))
    });

    tbl = html.table(tbl)

    output += html.h3('Current Positons')
    output += tbl
    output = openReport + btnRefresh + btnClear + output + closeReport

    addTextToHandout(output, 'Move History', 0);

    return
  }

  let initPosition = []; //{'id': mrId, 'pageid': obj.get('_pageid'), 'tokenid': obj.get('_id'), 'lastmove':obj.get('lastmove') }
  let lastPosition = []; //`{'tokenid': ${token.get('tokenid')}, 'left':${token.get('left')}, 'top':${token.get('top')}}


  function resetMoves() { // Reset moves to their ending positon
    // for each token in the lastPosition 
    lastPosition.forEach(lastPosRec =>{
      let t = getObj('graphic', lastPosRec.tokenid)
      if (t) {
        t.set('left', lastPosRec.left)
        t.set('top', lastPosRec.top)
      }
    })
  }

  function rewindMoves() {
    let pageid = Campaign().get("playerpageid")
    initPosition = [];
    lastPosition = []; // Need to think about when do we reset this.  Don't want ot lose or ending position.
    
    // For every token on the current page, catpure their positons in a lastPositoin data array
      // {id, tokenid, left, top}
    let tokens = findObjs({
      _type: 'graphic',
      _subtype: 'token',
      _pageid: pageid
    }).sort((a, b) => (a.get("name") > b.get("name") ? 1 : -1));

    tokens.forEach(token => {
      bFound = false;
      for (let i = 0; i++; i < lastPosition.length){
        if (token.get('_id') === lastPosition[i].tokenid){
          bFound = true;
          break;
        }
      }
      if (!bFound){
        let rec = `{'tokenid': ${token.get('tokenid')}, 'left':${token.get('left')}, 'top':${token.get('top')}}`
        lastPosition.push(rec);
      }
    });

    // Goto the beginning of the moveData array and find the first entry for this page
    // Add this tokenid & left,top position to a new initPosition data array
      // moveRec = {'id', 'pageid', 'tokenid', 'lastmove'}
      // initPositon = {'moveDataid', 'tokenid', 'lastmove'}
    state.myTokenEvents.moveData.forEach(moveRec=>{
      if(moveRec.pageid === pageid) {
        for (let i = 0; i < initPosition.length; i++) {
          if (initPosition[i].tokenid === moveRec.tokenid) {
            bfound = true;
            break;
          }
        }
        if (!bfound) {
          // for each record in the initPosition data array - set the tokens left & right values
          initPosition.push(moveRec);
          let coord = moveRec.lastmove.split(',');
          if (coord.length >= 2) {
            let t = getObj('graphic', moveRec.tokenid)
            if (t) {
              t.set('left', coord[0])
              t.set('right', coord[1])
            }
          }
        }
      }
    })
    return 0;
  }

  function nextMove(moveid) {

    let pageid = Campaign().get("playerpageid")

    //while moveData[moveid].pageid != playerpageid && and moveid <= movdData.length -1 
    moveid++;
    while (state.myTokenEvents.moveData[moveid].pageid != pageid && moveid <= state.myTokenEvents.moveData.length - 1) {
      moveid++;
    }

    // Did we find the next move record
    if (moveid <= state.myTokenEvents.moveData.length - 1) {
      // Yes - 
      let coord = state.myTokenEvents.moveData[moveid].lastmove.split(',');
      if (coord.length >= 2) {
        let t = getObj('graphic', state.myTokenEvents.moveData[moveid].tokenid)
        if (t) {
          t.set('left', coord[0])
          t.set('right', coord[1])
        }
      }
    } else {
      moveid = state.myTokenEvents.moveData.length
    }
    // return next moveid for this page or moveData.length
    return moveid;
  }

  function prevMove() {
    let pageid = Campaign().get("playerpageid")

    //while moveData[moveid].pageid != playerpageid && and moveid <= movdData.length -1 
    moveid--;
    while (state.myTokenEvents.moveData[moveid].pageid != pageid && moveid >= 0) {
      moveid--;
    }

    // Did we find the next move record
    if (moveid >= 0) {
      // Yes - move the token accordingly
      let coord = state.myTokenEvents[moveid].lastmove.split(',');
      if (coord.length >= 2) {
        let t = getObj('graphic', state.myTokenEvents[moveid].tokenid)
        if (t) {
          t.set('left', coord[0])
          t.set('right', coord[1])
        }
      }
    } else {
      moveid = 0
    }
    // return next moveid for this page or moveData.length
    return moveid;

  }

  function clearMoves() {
    state.myTokenEvents.moveData = [];
  }

  function addFollower(mTokenid, sTokenid, leftOffset, topOffset){

    let fRec = {};
    // Add a new follower.  If a records already exists, then just update the offsets
    if (leftOffset == undefined) {
      leftOffset = 0;
    }
    if (topOffset == undefined) {
      topOffset = 0;
    }

    let whisperTo = ''
    let player = getObj('player', gMsg.playerid)
    if (player) {
      whisperTo = player.get('_displayname')
    }

    let mName = getObj('graphic', mTokenid).get('name');
    let fName = getObj('graphic', sTokenid).get('name');
    let pageid = Campaign().get("playerpageid")
   // log(`adding follower: pid:${pageid} mtid:${mTokenid} stid:${sTokenid} lo:${leftOffset} to:${topOffset}`)

    // Lets see if the item we are adding already exists
    let item = getFollower(mTokenid, sTokenid)
   // log('Follow Item: ' + item)

    // Did we find a record?
    if (item === undefined || item.length === 0){

      // No - We also need to see if the selected folower is following anyone other token.
      item = getMaster(sTokenid)
      if (item === undefined || item.length === 0){ 

        // Final test - This Master Token can't also be a Follower of the targeted token
        item = getFollower(sTokenid, mTokenid);
        if (item === undefined || item.length === 0) {
          fRec = {'pageid':pageid, 'mTokenid':mTokenid, 'sTokenid':sTokenid, 'leftOffset': leftOffset, 'topOffset': topOffset}
          state.myTokenEvents.followData.push(fRec);
          let myMsg = `<b>${mName}</b> has a new follower: <b>${fName}</b>.`

          let a = `!Follow --delete ${mTokenid} ${sTokenid}`;
          let bDel = btnAPI({api: a, label: `Del`},{'border-radius': '1px', 'padding': '1px 1px'});
          a = `!Follow --edit ${mTokenid} ${sTokenid} ?{Left Offset|${leftOffset}} ?{Top Offset|${topOffset}}`;
          let bEdit = btnAPI({api: a, label: `Edit`},{'border-radius': '1px', 'padding': '1px 1px'});
          myMsg+= html.p(` ${bDel} ${bEdit} ${fName} offsets: ${leftOffset}, ${topOffset}`, {"font-size": "10px"})

          msgbox({msg: myMsg, whisperto: whisperTo, title: 'Add Follower', headercss: { 'background-color': defaultThemeColor1 }})
        } else {
          let myMsg = `<b>Unable to add follwer:</b> <b>${mName}</b> is already a follower of <b>${fName}</b>!!!`
          msgbox({msg: myMsg, whisperto: whisperTo, title: 'Error: Unable to add Follower', headercss: { 'background-color': defaultThemeColor1 }})
        }
      } else {
        // Yes - there is a different master defined, so lets just move it
        let oldMaster = getObj('graphic', item.mTokenid).get('name');
        delFollower(item.mTokenid, item.sTokenid)
        fRec = {'pageid':pageid, 'mTokenid':mTokenid, 'sTokenid':sTokenid, 'leftOffset': leftOffset, 'topOffset': topOffset}
        state.myTokenEvents.followData.push(fRec);

        let myMsg = `<b>${fName}</b> has been moved from <b>${oldMaster}</b> to <b>${mName}</b>.`
        msgbox({msg: myMsg, whisperto: whisperTo, title: 'Add Follower', headercss: { 'background-color': defaultThemeColor1 }})
      }
    } else {
      // Yes - update the associated informtion
      delFollower(mTokenid, sTokenid)
      fRec = {'pageid': pageid, 'mTokenid': mTokenid, 'sTokenid':sTokenid, 'leftOffset': leftOffset, 'topOffset': topOffset}
     // log(`  Follower already exists, updating offsets`)      
      state.myTokenEvents.followData.push(fRec);

      let myMsg = `Offset positions have been modified for <b>${fName}</b>. New offsets for left is <b>${leftOffset}</b> and top is <b>${topOffset}</b>.`

      let a = `!Follow --delete ${mTokenid} ${sTokenid}`;
      let bDel = btnAPI({api: a, label: `Del`},{'border-radius': '1px', 'padding': '1px 1px'});
      a = `!Follow --edit ${mTokenid} ${sTokenid} ?{Left Offset|${leftOffset}} ?{Top Offset|${topOffset}}`;
      let bEdit = btnAPI({api: a, label: `Edit`},{'border-radius': '1px', 'padding': '1px 1px'});
      myMsg+= html.p(` ${bDel} ${bEdit} ${fName} offsets: ${leftOffset}, ${topOffset}`, {"font-size": "10px"})


      msgbox({msg: myMsg, whisperto: whisperTo, title: 'Update Follower', headercss: { 'background-color': defaultThemeColor1 }})
    }
    moveFollower(mTokenid, sTokenid)
  }

  function delFollower(mTokenid, sTokenid){
    let pageid = Campaign().get("playerpageid")
   // log(`delFollower: ${mTokenid} ${sTokenid} count: ${state.myTokenEvents.followData.length}`)
    // followDataNew = state.myTokenEvents.followData.filter(item => item.pageid != pageid && item.mTokenid != mTokenid && item.sTokenid != sTokenid)
    let fdNew = []

    let whisperTo = ''
    let player = getObj('player', gMsg.playerid)
    if (player) {
      whisperTo = player.get('_displayname')
    }

    for (let i = 0; i<state.myTokenEvents.followData.length; i++) {
      let item = state.myTokenEvents.followData[i]
      if (item.pageid === pageid && item.mTokenid === mTokenid && item.sTokenid === sTokenid){
        // Filter this record out
        try {
          let mName = getObj('graphic', mTokenid).get('name');
          let fName = getObj('graphic', sTokenid).get('name');
          let myMsg = `<b>${fName}</b> is no longer following <b>${mName}</b>`
        msgbox({msg: myMsg, whisperto: whisperTo, title: 'Remove Follower', headercss: { 'background-color': defaultThemeColor1 }})
        } catch {
        msgbox({msg: 'Error removing follower', whisperto: whisperTo, title: 'Error', headercss: { 'background-color': defaultThemeColor1 }})
        }

      } else {
        fdNew.push(item)
      }
    }
   // log(`delFollower: Remaining count: ${fdNew.length}`)
    state.myTokenEvents.followData = fdNew;
  }

  function getFollower(mTokenid, sTokenid){
    let pageid = Campaign().get("playerpageid")
   // log(`getFollower: pageid: ${pageid} mTokenid ${mTokenid} sTokenid ${sTokenid}`)  // Why can't I get this item?
    
    // let f =  followData.filter(item => item.pageid === pageid && item.mTokenid === mTokenid && item.sTokenid === sTokenid)
    for (let i = 0; i < state.myTokenEvents.followData.length; i++) {
      if (state.myTokenEvents.followData[i].pageid == pageid && state.myTokenEvents.followData[i].mTokenid == mTokenid && state.myTokenEvents.followData[i].sTokenid == sTokenid){
       // log(`getFollower found:`)  // Why can't I get this item?
        let f = state.myTokenEvents.followData[i];
        return f;
      }
    }
   // log(`getFollower Not found: mTokenid ${mTokenid} sTokenid ${sTokenid}`)  // Why can't I get this item?
  }
  function getFollowers(mTokenid){
    // Returns a list of followers for a selected tokena and page
    let pageid = Campaign().get("playerpageid")
    let f=[];
    // let f = followData.filter(item => item.pageid === pageid && item.mTokenid === mTokenid)
    state.myTokenEvents.followData.forEach(item =>{
      if (item.pageid == pageid && item.mTokenid == mTokenid){
        f.push(item);
      }
    })
   // log(`getFollowers: Count: ${f.length}`)
    return f
  }
  function getMaster(sTokenid){
    // Returns a list of followers for a selected tokena and page
    let pageid = Campaign().get("playerpageid")
    for (let i = 0; i < state.myTokenEvents.followData.length; i++) {
      let item = state.myTokenEvents.followData[i]
      if (item.pageid === pageid && item.sTokenid === sTokenid) {
        return item;
      }
    }
  }

  function moveFollower(mTokenid, sTokenid){
   // log(`moveFollower: mTokenid: ${mTokenid} sTokenid: ${sTokenid}`)
    let item = getFollower(mTokenid, sTokenid)
    if (item === undefined) return;
    // dumpObject(item);
    
    let mToken = getObj('graphic', mTokenid)
    let sToken = getObj('graphic', sTokenid)

    try {    
      if ((mToken) || (sToken)){
        let newLeft = Number(item.leftOffset) + Number(mToken.get('left'))
        let newTop = Number(item.topOffset) + Number(mToken.get('top'))
      // log(`${item.leftOffset} ${item.topOffset} ${mToken.get('left')} ${mToken.get('top')}`)
      // log(`Move follower to ${newLeft} and ${newTop}`)

        let PrevPositon = `${sToken.get('left')},${sToken.get('top')}`
        sToken.set('top', newTop)
        sToken.set('left', newLeft)

        let mrId = Number(state.myTokenEvents.moveData.length) + 1
        let moveRec = {'id': mrId, 'pageid': sToken.get('_pageid'), 'tokenid': sToken.get('_id'), 'lastmove': PrevPositon }
        state.myTokenEvents.moveData.push(moveRec)

      } else {
      // log(`MoveFolloer: Master${mTokenid} or Shaddow${sTokenid} token not found`)
      }
    } catch (err) {
      myDebug(4, `moveFolloer: ${err.message}`);
    }
  
    // Move the follower's followers
    let followers = getFollowers(sTokenid);
    followers.forEach(f => {
      moveFollower(sTokenid, f.sTokenid)
    });
  }

  function moveMaster(mTokenid, sTokenid){
   // log(`moveMaster: mTokenid: ${mTokenid} sTokenid: ${sTokenid}`)

    let item = getFollower(mTokenid, sTokenid)
    if (item === undefined) return
    let mToken = getObj('graphic', mTokenid)
    let sToken = getObj('graphic', sTokenid)

    try{
      if ((mToken) || (sToken)){
      // log(`MoveMaseter: Master${mTokenid}`)
        let newLeft =  (Number(sToken.get('left')) - Number(item.leftOffset))
        let newTop =  (Number(sToken.get('top')) - Number(item.topOffset))
        let PrevPositon = `${mToken.get('left')},${mToken.get('top')}`

        mToken.set('left', newLeft)
        mToken.set('top', newTop)

        let mrId = Number(state.myTokenEvents.moveData.length) + 1
        let moveRec = {'id': mrId, 'pageid': mToken.get('_pageid'), 'tokenid': mToken.get('_id'), 'lastmove': PrevPositon }
        state.myTokenEvents.moveData.push(moveRec)


      } else {
      // log(`moveMaster: Master${mTokenid} or Shaddow${sTokenid} token not found`)
      }
    } catch (err) {
      myDebug(4, `moveMaster: ${err.message}`);
    }

  }

  function buildDMToolsDialog(){
    let myMsg = ''
    let tbl = ''
    let msgHandouts = '';
    let api = ''
    let btn = ''
    myMsg+= html.p('A collection of common commands/tools I use or have built', {'font-size': '11px'})

    tbl = html.tr(html.th("Run") + html.th("Description"))

    api = `!DMDash --open`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard'))

    api = `!DMDash --help`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard Help'))

    api = `!DMDash --config`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard Config'))

    api = `!DMDash --clearcache`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Dashboard Clear Cache'))

    api = `!DMDash --track`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Jukebox'))

    api = `!DMDash --ssm`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Status Markers'))

    api = `!DMnotes --build`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('DM Notes'))

    api = `!DMDash --playerhandout`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Player Handout'))

    api = `!DMDash --resourcemgt`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Party Resource Mgt'))


    api = `!CAMPAIGNHEALTH`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Campaign Database Health'))

    // Health Check
    api = `!HEALTHCHECK`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Update health indicators for all tokens on current page'))

    // Followers
    api = `!Follow --show`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Show followers on current page'))

    api = `!follow --showmovements`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Token Movements'))

    api = `!follow --clearmovements`;
    btn = btnAPI({api: api, label: `!`});
    tbl += html.tr(html.td(btn) + html.td('Clear Movement Data'))


    tbl = html.table(tbl)

    myMsg+=tbl

    msgbox({msg: myMsg, 
          title: 'DM Tools',
          whisperto: `GM`,          
          headercss: { 'background-color': defaultThemeColor1 }})
  }

  function showFollowers(){
    let myMsg = ''
    let tbl = ''

    myMsg+= html.p('Lists followers for current page', {'font-size': '11px'})

    // List Followers
    tbl = html.tr(html.th("Master") + html.th("Follower"))

    let mName = ''
    let fName = ''
    state.myTokenEvents.followData.forEach(item => {
      try {
        mName = getObj('graphic', item.mTokenid).get('name');
      } catch {
        mName = 'Missing'
      }

      try{
        fName = getObj('graphic', item.sTokenid).get('name');
      } catch {
        fName = 'Missing'
      }
      if (item.pageid === Campaign().get("playerpageid")){
        tbl+=html.tr(html.td(mName)+html.td(fName))
      }

    })

    tbl = html.table(tbl)
    myMsg+=tbl

    let api = `!Follow --clear`;
    let btn = btnAPI({api: api, label: `Clear All Data`});

    msgbox({msg: myMsg, 
          title: 'Show Followers',
          btn: btn,
          whisperto: `GM`,
          headercss: { 'background-color': defaultThemeColor1 }})
  }

  function buildUserDialog(selTokenId){   // Create a clean looking dialog with the following features
    // Allows a player to:
    //   Select a follower for the selected token 
    //   Remove a follower for the selected token
    //   Lists current followers and their current offsets
    //   Edit/Adj offsets for each follower
    let myMsg='';
    let selToken = getObj('graphic', selTokenId);
    let selTokenName = ''

    let whisperTo = ''
    let player = getObj('player', gMsg.playerid)
    if (player) {
      whisperTo = player.get('_displayname')
    }

    if (selToken) {
      selTokenName = selToken.get('name');
    }
   // log(`buildUserDialog: ${selTokenId} / ${selTokenName}`)

    myMsg=html.h2(selTokenName)
    myMsg+= html.p('From this dialog, you can add, edit and remove followers')

    // List Followers
    let followers = getFollowers(selTokenId)
    if (followers.length > 0) {
      myMsg+= html.h3('My Followers:')
      followers.forEach( f=> {
        let fName = getObj('graphic', f.sTokenid).get('name');
        let a = `!Follow --delete ${selTokenId} ${f.sTokenid}`;
        let bDel = btnAPI({api: a, label: `Del`},{'border-radius': '1px', 'padding': '1px 1px'});
        a = `!Follow --edit ${selTokenId} ${f.sTokenid} ?{Left Offset|${f.leftOffset}} ?{Top Offset|${f.topOffset}}`;
        let bEdit = btnAPI({api: a, label: `Edit`},{'border-radius': '1px', 'padding': '1px 1px'});
        myMsg+= html.p(` ${bDel} ${bEdit} ${fName} offsets: ${f.leftOffset}, ${f.topOffset}`, {"font-size": "10px"})
      })
    }

    // List Masters
    let master = getMaster(selTokenId)
    if (master != undefined) {
      myMsg+= html.h3('My Master:')
      let mName = getObj('graphic', master.mTokenid).get('name');
      myMsg+= html.p(` - ${mName} offsets: ${master.leftOffset}, ${master.topOffset}`)
    }

    let api = `!Follow --Add @{selected|token_id} @{target|token_id}`;
    let btn = btnAPI({api: api, label: `Add Follower`});
    
    msgbox({msg: myMsg, 
          title: 'Manage Followers',
          whisperto: whisperTo,
          btn: btn, 
          headercss: { 'background-color': defaultThemeColor1 }})
   
  }

  function setHealthIndicatorsForPage(){
    let pageid = Campaign().get("playerpageid")
    let tokens = findObjs({
      _type: 'graphic',
      _pageid: pageid,
      _subtype: 'token'
    })
    // log(`Setting health indicators Count: ${tokens.length}`)
    tokens.forEach(token => {
      if (token.get("bar1_max") !== undefined) {
        if (token.get("bar1_max") > 0) {
          setHealthIndicator(token)
        }
      }
    });
  }

  function setHealthIndicator(token){

    //Bar 1 should contain the hp - calculate health pct
    let hp = Number(token.get('bar1_value'))
    let hp_max = Number(token.get('bar1_max'))
    hpPct = hp / hp_max * 100; 
    //token.set('bar3_value', hpPct);
    // log(`SetHealthIndicator: ${token.get('name')} pct: ${hpPct} bar1_max: ${hp_max} bar1_value: ${hp}`)

    removeHealthMarker(token); 

    if (hpPct >75 && hpPct <= 95) {
      setHealthMarker(token, smH1); return
    } else if (hpPct >50 && hpPct <= 75) {
      setHealthMarker(token, smH2); return
    } else if (hpPct >25 && hpPct <= 50) {
      setHealthMarker(token, smH3); return
    } else if (hpPct >0 && hpPct <= 25) {
      setHealthMarker(token, smH4); return
    } else if (hpPct <=0) {
      setHealthMarker(token, smDead); return
    }
  }
  function removeHealthMarker(token){
    let markers = token.get('statusmarkers');
    let newMarkers = ''
    
    if (markers.indexOf(smH1) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH1).join(',');
      token.set('statusmarkers', newMarkers);
    }    
    if (markers.indexOf(smH2) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH2).join(',');
      token.set('statusmarkers', newMarkers);
    }    
    if (markers.indexOf(smH3) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH3).join(',');
      token.set('statusmarkers', newMarkers);
    }    
    if (markers.indexOf(smH4) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smH4).join(',');
      token.set('statusmarkers', newMarkers);
    }
    if (markers.indexOf(smDead) !== -1) {
      newMarkers = markers.split(',').filter(marker => marker !== smDead).join(',');
      token.set('statusmarkers', newMarkers);
    }
  }

  function setHealthMarker(token, marker){
    let markers = token.get('statusmarkers');
    let newMarkers = '';
    // log (`SetHealthMarker ${token.get('name')} ${marker}`);
    newMarkers = markers.split(',');
    newMarkers.push(marker);
    newMarkers = newMarkers.join(',');
    // log (`SetHealthMarker ${newMarkers}`);
    token.set('statusmarkers', newMarkers);
  }

  function makeMenuButton(name, link, selected, minwidth) {
    // let buttonStyle = `background-color: red; color:yellow !important; font-weight:normal; border-radius: 1px; padding: 1px; margin: 1px 1px 1px 0px; display: inline-block`;  
    // let buttonStyle = `display: flex; flex-direction: column; align-items: center; padding: 6px 14px; font-family: -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif; border-radius: 6px; border: none; background: #6E6D70; box-shadow: 0px 0.5px 1px rgba(0, 0, 0, 0.1), inset 0px 0.5px 0.5px rgba(255, 255, 255, 0.5), 0px 0px 0px 0.5px rgba(0, 0, 0, 0.12); color: #DFDEDF; user-select: none; -webkit-user-select: none; touch-action: manipulation;`
    let buttonStyle = `background-color: #521E10; border: 1px; color: white; text-align: center; display: inline-block; font-size: 11px; margin: 2px 1px; cursor: pointer; padding: 3px 6px; border-radius: 4px;`

    if (selected == true || selected == 1){
      buttonStyle = `background-color: #FFAD00; border: 1px; color: black; text-align: center; display: inline-block; font-size: 11px; margin: 2px 1px; cursor: pointer; padding: 3px 6px; border-radius: 4px;`
    }

    if (minwidth === "f") {
      minwidth = "100%"
    } else {
      if (!minwidth) {
        minwidth = 'NONE'
      }
      minwidth = minwidth + "px"
    }

    if (minwidth == 'NONE'){
      minwidth = '';
    } else {
      minwidth =`width:${minwidth}`
    }

    if (!name) {
      name = "untitled"
    }

    if (link) {
      return `<a style = '${buttonStyle}; ${minwidth} !important' href='${link}'>${name}</a>`;
    } else {
      return `<div style = '${buttonStyle}; ${minwidth}; display:inline-block !important'>${name}</div>`;
    }
  }

  function dumpObject(obj){
    let output = ''
    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        output+= `  Field Name: ${key} => ${obj[key]}<br>`
        log (`  Field Name: ${key} => ${obj[key]}`)
      }
    }
    return output;
  }

  function dumpFollowData(){
    let output = ''
    let i = 0

    log (`Dumping followData: Array Count is ${state.myTokenEvents.followData.length}`)
    state.myTokenEvents.followData.forEach(item => {
     // log(`FollowData[${i}]: pageid: ${item.pageid} mTokenid: ${item.mTokenid} sTokenid: ${item.sTokenid} topOffset: ${item.topOffset} leftOffset: ${item.leftOffset}`)
      i++;
    })

  }

  function addTextToHandout(noteTxt, handoutName, mode){
    // mode - Determines how txt will be added to the handout
    //  0: Complete replacement
    //  1: Append to bottom
    //  2: Push to the top
    //  3: Append to the bottom and include a timestamp

    const ho = getHandout(handoutName);
    let newText = '';

    // Add the text to the note 
    ho.get("notes", function(notes) {
      switch(mode){
      case 0:
      case '0':
        newText = noteTxt;
        break;
      case 1:
      case '1':
        newText = `${notes}<br>${noteTxt}`
        //myDebug(4, `addTextToHandout: Mode 1 (${handoutName}, mode:${mode}, len:(${newText.length})`)        
        break;
      case 2:
      case '2':
        //myDebug(4, `addTextToHandout: Mode 2 (${handoutName}, mode:${mode}, len:(${newText.length})`)
        newText = `${noteTxt}<br>${notes}`
        break;
      case 3:
      case '3':
        //myDebug(4, `addTextToHandout: Mode 3 (${handoutName}, mode:${mode}, len:(${newText.length})`)
        let sysDate = GetSystemUTCDate();
        newText = `${notes}<br>${sysDate}: ${noteTxt}`
        break;
      }
      //myDebug(4, `addTextToHandout: set (${handoutName}, mode:${mode}, len:(${newText.length})`)
      setTimeout(()=>ho.set("notes", newText),0);
    });
  }
  function getHandout(handoutName) {
    let handout = findObjs({
        _type: 'handout',
        name: handoutName
    })[0];

    if (!handout) {
        handout = createObj('handout', {
            name: handoutName,
            archived: false
        });
    }
    return handout;
  }

  function myDebug(lvl,txt){
    // giDebug: 0 - Off, 1 - API Log, 2 - Chat, 3 - Chat & Log, 4 - Handout (future)
    // giDebugLvl: 0 - All, 1 - Low Info, 2 - High Info, 3 - Basic Debug, 4 - New Code Debug, 5 - Performance logging
  
    if ((gDebug == 1 || gDebug == 3) && lvl >= gDebugLvl) {
      log(txt);
    }
    if ((gDebug == 2 || gDebug == 3) && lvl >= gDebugLvl) {
      sendChat('Debug','/w gm '+txt);
    }
    if (gDebug == 4 && lvl >= gDebugLvl) {
      // Add to Handout
      addTextToHandout(`${txt} lvl(${lvl}`, "DM Debug Log", 3) 
    }
  }
  function mySendChat(whisper, title, message, links){
    const openChat = `<div style="border-radius: 10px ; border: none ; background-color: ; overflow: hidden ; width: 100%"><div style = "border-radius: 10px ; border: 2px solid #000000 ; background-color:  #00000000; overflow: hidden ; margin: 0px 16px 16px 0px ; box-shadow: 5px 8px 8px #888888">`
    const closeChat= `<\div><\div>`;
    const tblChatStyle = `width: 100% ; margin: 0 auto ; border-collapse: collapse ; font-size: 12px;`
    const trhChatStyle = `border-bottom: 1px solid #000000 ; font-weight: bold ; line-height: 22px ; background-color: #521e10 ; color: #ffffff;`
    const tdChatStyle = `padding: 4px ; min-width: 10px;`
    
    let chatTxt = '';

    if(whisper == true){
      chatTxt = `/w gm `
    }
    chatTxt += `${openChat}<table style="${tblChatStyle}"><tr style="${trhChatStyle}"><td style="${tdChatStyle}">${title}</td></tr>`
    chatTxt += `<tr><td style="${tdChatStyle}">${message}</td></tr>`
    if (links){
      chatTxt += `<tr><td style="${tdChatStyle}">${links}</td></tr>`
    }
    chatTxt += `</table>${closeChat}`
    sendChat('DMDashboard', chatTxt);
  }

      // ============================================
    //      PRESENTATION
    // ============================================
    /*
        - color management
        - CSS
        - HTML
        - messaging
        - help construction
    */

    // COLOR MANAGEMENT ===========================
    const getAltColor = (primarycolor, fade = .35) => {

      let pc = hexToRGB(`#${primarycolor.replace(/#/g, '')}`);
      let sc = [0, 0, 0];

      for (let i = 0; i < 3; i++) {
          sc[i] = Math.floor(pc[i] + (fade * (255 - pc[i])));
      }

      return RGBToHex(sc[0], sc[1], sc[2]);
  };
  const RGBToHex = (r, g, b) => {
      r = r.toString(16);
      g = g.toString(16);
      b = b.toString(16);

      if (r.length === 1)
          r = "0" + r;
      if (g.length === 1)
          g = "0" + g;
      if (b.length === 1)
          b = "0" + b;

      return "#" + r + g + b;
  };
  const getTextColor = (h) => {
      h = `#${h.replace(/#/g, '')}`;
      let hc = hexToRGB(h);
      return (((hc[0] * 299) + (hc[1] * 587) + (hc[2] * 114)) / 1000 >= 128) ? "#000000" : "#ffffff";
  };
  const hexToRGB = (h) => {
      let r = 0, g = 0, b = 0;

      // 3 digits
      if (h.length === 4) {
          r = "0x" + h[1] + h[1];
          g = "0x" + h[2] + h[2];
          b = "0x" + h[3] + h[3];
          // 6 digits
      } else if (h.length === 7) {
          r = "0x" + h[1] + h[2];
          g = "0x" + h[3] + h[4];
          b = "0x" + h[5] + h[6];
      }
      return [+r, +g, +b];
  };
  const validateHexColor = (s, d = defaultThemeColor1) => {
      let colorRegX = /(^#?[0-9A-Fa-f]{6}$)|(^#?[0-9A-Fa-f]{3}$)|(^#?[0-9A-Fa-f]{6}\d{2}$)/i;
      return '#' + (colorRegX.test(s) ? s.replace('#', '') : d);
  };

  // CSS ========================================
  const defaultThemeColor1 = '#521e10';
  const defaultThemeColor2 = '#84c6a6';
  const defaultThemeColor3 = '#ffc286';
  const defaultThemeColor4 = '#fff1af';
  const defaultbgcolor = "#ce0f69";

  const defaultdivCSS = {
      "border-radius": `2px`,
      "border": `1px solid #000000`,
      "background-color": '#00000000',
      "overflow": `hidden`
  };
  const defaulttableCSS = {
      'width' : '100%',
      'margin' : '0 auto',
      "border-collapse": 'collapse',
      "font-size": '11px',
  };
  const defaultimgCSS = {};
  const defaultpCSS = {"font-size": "10px"};
  const defaultaCSS = {};
  const defaulth1CSS = {};
  const defaulth2CSS = {};
  const defaulth3CSS = {
    'color': '#521E10'};
  const defaulth4CSS = {
      'color': '#521E10'};
  const defaulth5CSS = {};
  const defaultthCSS = {
      "border-bottom": `1px solid #000000`,
      "font-weight": `bold`,
      "text-align": `center`,
      "line-height": `22px`
  };
  const defaulttrCSS = {};
  const defaulttdCSS = {
      'padding' : '2px',
      'min-width': '10px'
  };
  const defaultcodeCSS = {};

  const defaultMessageHeaderCSS = {
      'border-bottom': `1px solid #000000`,
      'font-weight': `bold`,
      'line-height': `22px`,
      'background-color': '#dedede'
  };
  const defaultMessageBodyCSS = {};
  const defaultButtonCSS = {
      'background-color': defaultThemeColor1,
      'border-radius': '6px',
      'min-width': '5px',
      'padding': '3px 4px'
  };

  const menuBoxCSS = {
    'position': 'fixed',
    'top': '0px',
    'left': '0px',
    'height': '30px',
    'color': '#000', 
    'border': '1px solid #000',
    'background-color': '#FFEBD6',
    'box-shadow': '0 0 2px #000',
    'display': 'block',
    'text-align': 'left',
    'font-size': '13px',
    'padding': '2px',
    'margin-bottom': '2px',
    'font-family': 'sans-serif',
    'white-space': 'pre-wrap'
  }

  const menuButtonCSS = {
    'background-color': '#521E10',
    'border': '1px',
    'color': 'white',
    'text-align': 'center',
    'display': 'inline-block',
    'font-size': '14px',
    'margin': '2px 1px',
    'cursor': 'pointer',
    'padding': '3px 6px',
    'border-radius': '4px'
  };

  const menuButtonCSS_Selected = {
    'background-color': '#521E10',
    'border': '1px',
    'color': 'black',
    'text-align': 'center',
    'display': 'inline-block',
    'font-size': '14px',
    'margin': '2px 1px',
    'cursor': 'pointer',
    'padding': '3px 6px',
    'border-radius': '4px'
  };

  const shadoweddivCSS = {
      'margin': '0px 16px 16px 0px',
      'padding': '5px 5px',
      'box-shadow': '5px 8px 8px #888888'
  };
  const boundingdivCSS = {
      'width' : '100%',
      'border' : 'dashed 0px'
  };
  const imgDivContainer = {
    'width': '50%', // Make the image width responsive to the container's width
    'height': 'auto', // Maintain the image's aspect ratio
    'overflow': 'hidden' // Hide any part of the image that goes beyond the container boundaries
  };
  const img1CSS = {
    'border' : '1px solid black',
  }

  const divC1 = {
    'flex' : '0 0 25%',
    'max-width' : '25%'
  }

  const divC2 = {
    'flex' : '0 0 75%',
    'max-width' : '75%'
  }

  const divC3 = {
    'display' : 'flex',
    'flex-direction' : 'row',
    'align-items' : 'center'
  }

  const divScrollBoxCSS = {
    'height' : '80vh',
    'overflow-y' : 'scroll',
    'border' : '1px solid black',
    'padding' : '5px',
    'display' : 'block'    
  }
  const combineCSS = (origCSS = {}, ...assignCSS) => {
      return Object.assign({}, origCSS, assignCSS.reduce((m, v) => {
          return Object.assign(m, v || {});
      }), {});
  };
  const confirmReadability = (origCSS = {}) => {
      let outputCSS = Object.assign({}, origCSS);
      if (outputCSS['background-color']) outputCSS['background-color'] = validateHexColor(outputCSS['background-color'] || "#dedede");
      if (outputCSS['color'] || outputCSS['background-color']) outputCSS['color'] = getTextColor(outputCSS['background-color'] || "#dedede");
      return outputCSS;
  };
  const assembleCSS = (css) => {
      return `"${Object.keys(css).map((key) => { return `${key}:${css[key]};` }).join('')}"`;
  };
  // HTML =======================================
  const html = {
      div: (content, CSS) => `<div style=${assembleCSS(combineCSS(defaultdivCSS, (CSS || {})))}>${content}</div>`,
      h1: (content, CSS) => `<h1 style=${assembleCSS(combineCSS(defaulth1CSS, (CSS || {})))}>${content}</h1>`,
      h2: (content, CSS) => `<h2 style=${assembleCSS(combineCSS(defaulth2CSS, (CSS || {})))}>${content}</h2>`,
      h3: (content, CSS) => `<h3 style=${assembleCSS(combineCSS(defaulth3CSS, (CSS || {})))}>${content}</h3>`,
      h4: (content, CSS) => `<h4 style=${assembleCSS(combineCSS(defaulth4CSS, (CSS || {})))}>${content}</h4>`,
      h5: (content, CSS) => `<h5 style=${assembleCSS(combineCSS(defaulth5CSS, (CSS || {})))}>${content}</h5>`,
      p: (content, CSS) => `<p style=${assembleCSS(combineCSS(defaultpCSS, (CSS || {})))}>${content}</p>`,
      table: (content, CSS) => `<table style=${assembleCSS(combineCSS(defaulttableCSS, (CSS || {})))}>${content}</table>`,
      th: (content, CSS) => `<th style=${assembleCSS(combineCSS(defaultthCSS, (CSS || {})))}>${content}</th>`,
      tr: (content, CSS) => `<tr style=${assembleCSS(combineCSS(defaulttrCSS, (CSS || {})))}>${content}</tr>`,
      td: (content, CSS) => `<td style=${assembleCSS(combineCSS(defaulttdCSS, (CSS || {})))}>${content}</td>`,
      td2: (content, CSS) => `<td colspan="2" style=${assembleCSS(combineCSS(defaulttdCSS, (CSS || {})))}>${content}</td>`,
      code: (content, CSS) => `<code style=${assembleCSS(combineCSS(defaultcodeCSS, (CSS || {})))}>${content}</code>`,
      a: (content, CSS, link) => `<a href="${link}" style=${assembleCSS(combineCSS(defaultaCSS, (CSS || {})))}>${content}</a>`,
      img:(content, CSS, link, title, width, height) => `<img src="${link}" style=${assembleCSS(combineCSS(defaultimgCSS, (CSS || {})))} alt="${content} width="${width}" height="${height}" title="${title}">`
  }
  // HTML Escaping function
  const HE = (() => {
      const esRE = (s) => s.replace(/(\\|\/|\[|\]|\(|\)|\{|\}|\?|\+|\*|\||\.|\^|\$)/g, '\\$1');
      const e = (s) => `&${s};`;
      const entities = {
          '<': e('lt'),
          '>': e('gt'),
          "'": e('#39'),
          '@': e('#64'),
          '{': e('#123'),
          '|': e('#124'),
          '}': e('#125'),
          '[': e('#91'),
          ']': e('#93'),
          '"': e('quot')
      };
      const re = new RegExp(`(${Object.keys(entities).map(esRE).join('|')})`, 'g');
      return (s) => s.replace(re, (c) => (entities[c] || c));
  })();
  // MESSAGING ==================================
  const btnAPI = ({ api: api = "", label: btnlabel = "Run API", css: css = defaultButtonCSS } = {}) => {
      let btnCSS = confirmReadability(css);
      //r20css = {};
      //if(['t', 'true', 'y', 'yes', true].includes(r20style)) Object.assign(r20css, {padding: '5px', display: 'inline-block', border: '1px solid white;'});
      return html.a(btnlabel, btnCSS, HE(api));
  };
  const msgbox = ({
      msg: msg = "message", 
      title: title = "DMDash Output",
      btn: btn = "buttons",
      sendas: sendas = "API",
      whisperto: whisperto = "",
      headercss: headercss = {},
      bodycss: bodycss = {}
  }) => {
      let hdrCSS = confirmReadability(combineCSS(defaultMessageHeaderCSS, headercss));
      let bodyCSS = confirmReadability(combineCSS(defaultMessageBodyCSS, bodycss));

      let hdr = html.tr(html.td(title, {}), hdrCSS);
      let body = html.tr(html.td(msg, {}), bodyCSS);
      let buttons = btn !== "buttons" ? html.tr(html.td(btn, { 'text-align': `right`, 'margin': `4px 4px 8px`, 'padding':'8px' }), {}) : "";

      let output = html.div(html.div(html.table(`${hdr}${body}${buttons}`, {}), shadoweddivCSS), boundingdivCSS);
      if (whisperto) output = `/w "${whisperto}" ${output}`;
      sendChat(sendas, output);
  };

});
{try{throw new Error('');}catch(e){API_Meta.myTokenEvents.lineCount=(parseInt(e.stack.split(/\n/)[1].replace(/^.*:(\d+):.*$/,'$1'),10)-API_Meta.myTokenEvents.offset);}}