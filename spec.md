# 3 Card Blind Core Game Engine Modifications - Detailed Specification

## 2. Core Game Engine Modifications

### 2.1 Game State Initialization

#### 2.1.1 Initial Setup Requirements
The 3CB game must initialize with fundamentally different parameters than standard Magic:

**Player State Initialization:**
- Both players start at 20 life (standard Magic rule maintained)
- Each player begins with exactly 3 cards in hand
- No libraries/decks exist in the game state
- No graveyards initially (created as cards are discarded/destroyed)
- Standard zones exist: battlefield, hand, graveyard, exile, stack
- No command zone needed (no commanders in 3CB)

**Turn Structure:**
- Game begins with Player 1 (chosen arbitrarily or by external tournament rules)
- Standard Magic turn phases apply: untap, upkeep, draw, main 1, combat, main 2, end
- Draw step is modified (see section 2.2.2)

**Game Rules Modifications:**
- No mulligan phase (players cannot mulligan with only 3 cards)
- No opening hand size validation beyond the 3-card deck requirement
- Standard Magic timing rules apply
- Priority passing follows standard Magic rules

#### 2.1.2 Deck Loading Process
**Pre-Game Deck Validation:**
- Verify each deck contains exactly 3 cards
- Check cards against current 3CB banlist
- Validate card legality in Forge's database
- No sideboard processing required

**Card Distribution:**
- All 3 cards from each player's "deck" go directly to their opening hand
- No shuffling required
- No library zone created for either player

### 2.2 Library/Deck Rule Modifications

#### 2.2.1 Library Zone Handling
**Library Zone State:**
- No library zones exist in the initial game state
- Library zones are created dynamically when cards would be put into them
- Library count reports 0 for non-existent libraries
- Once created, libraries function normally and persist for the remainder of the game

**Library-Related Effects:**
- Cards that reference library size work normally (returning 0)
- Cards that would shuffle libraries have no effect if library is empty
- Cards that would search libraries find nothing if library is empty
- Cards that put cards "on top of library" create a library with that card

#### 2.2.2 Draw Step and Draw Effects
**Standard Draw Step Behavior:**
- Players still have a draw step each turn
- Drawing from an empty library produces no effect (no card drawn, no life loss)
- Turn continues normally after failed draw attempt
- No state-based effect checks for library depletion

**Draw Effect Handling:**
- "Draw a card" effects on non-existent/empty library: effect resolves successfully but no card is drawn
- The spell or ability completes its resolution normally, performing all other effects
- "Draw X cards" effects: draw as many as possible (0 from empty/non-existent library), then continue with remaining effects
- Replacement effects that modify draws work normally
- "If you would draw" triggered abilities still trigger, even if no card is drawn

**Specific Draw Interactions:**
- Card effects that say "draw cards equal to..." work but draw 0
- Effects that care about the number of cards drawn still count 0 as the number drawn
- Laboratory Maniac and similar "draw from empty library" effects do not trigger
- Howling Mine effects trigger but produce no cards

#### 2.2.3 Library Population During Game
**Cards Entering Library:**
- If effects put cards into libraries during the game, create/populate library normally
- Subsequent draws work as expected with populated library
- Library count updates correctly
- Standard library mechanics resume once library contains cards

**Examples of Library Population:**
- Gaea's Blessing puts itself back when milled
- Brainstorm puts cards on top of library
- Mystical Tutor places card on top of library
- Elixir of Immortality shuffles cards into library

### 2.3 Perfect Information Implementation

#### 2.3.1 Hand Visibility Requirements
**Information Exposure:**
- Both players' hands are visible to both players at all times
- Hand contents must be displayed prominently in the UI
- No "hidden hand" state exists in 3CB
- All cards in hand show full oracle text and current characteristics

**Information Access:**
- Game engine must provide read access to opponent's hand for decision-making
- UI must clearly distinguish between "your hand" and "opponent's hand"
- Card targeting can reference cards in opponent's hand where legally allowed
- No "hand size" hidden information - both players always know exact hand contents

#### 2.3.2 Hidden Information Override System
**Principle**: All information that would normally be hidden becomes public in 3CB

**Hidden Choice Elimination:**
- Secret choices must be made publicly
- Random selections become opponent choices (see section 3)
- Modal spells with hidden modes must declare modes publicly
- Face-down cards reveal their identity immediately

**APNAP Order for Public Choices:**
When multiple players must make simultaneous "hidden" choices:
1. Active player makes their choice first, publicly
2. Non-active player(s) make their choices second, publicly, with full knowledge of active player's choice
3. All choices are locked in before any resolve
4. Choices resolve simultaneously as per normal Magic rules

#### 2.3.3 Specific Hidden Information Cases
**Face-Down Cards:**
- Morph creatures are played face-up (identity known)
- Manifest effects reveal the manifested card
- Face-down spells from effects reveal themselves
- Cloak/disguise mechanics work face-up

**Secret Information Effects:**
- Telepathy effects have no impact (hands already visible)
- Peek effects have no impact (hands already visible)
- Scrying shows cards to both players
- "Look at target player's hand" effects are redundant

**Modal and Choice-Based Effects:**
- Charm spells declare all modes publicly when cast
- "Choose secretly" effects become "choose publicly"
- Voting effects proceed with open voting
- Bid effects proceed with open bids

### 2.4 Game State Validation

#### 2.4.1 State-Based Effects Modifications
**Standard SBEs Apply:**
- Creature death due to damage/toughness
- Planeswalker loyalty checks
- Legend rule enforcement
- Aura attachment validation

**Modified SBEs:**
- Remove library depletion loss condition
- All other loss conditions remain (life total, poison, etc.)
- Empty hand condition checks work normally

#### 2.4.2 Win Condition Modifications
**Maintained Win Conditions:**
- Life total reaches 0 or below
- Poison counters reach 10 or more
- Specific card-based win conditions (Test of Endurance, etc.)
- Concession (if manual play mode)

**Removed Win Conditions:**
- Drawing from empty library (does not cause loss)
- Unable to draw required cards (game continues)

**Draw/Stalemate Conditions:**
- Game reaches a state where no player can make progress toward winning
- Both players pass priority with empty stack and no legal actions that advance game state
- Infinite loops that don't change game state

#### 2.4.3 Game State Tracking
**Required State Information:**
- Complete hand contents for both players
- Battlefield state (all permanents and their states)
- Graveyard contents (both players)
- Exile zone contents
- Life totals and counters
- Stack contents and order

**State History:**
- Maintain complete game state history for analysis
- Track decision points and alternatives
- Record all legal actions available at each decision point
- Enable game state rewind for analysis purposes

### 2.5 Timing and Priority Modifications

#### 2.5.1 Priority Passing Rules
**Standard Priority Rules Apply:**
- Active player receives priority first
- Priority passes in turn order
- Players must pass priority for stack to resolve
- Fast effects and slow effects follow normal timing

**3CB-Specific Considerations:**
- Perfect information doesn't change priority timing
- Players still make decisions in real-time order
- No simultaneous decision-making except where already specified in Magic rules

#### 2.5.2 Instant Speed Responses
**Response Windows:**
- All standard Magic response windows exist
- Players can respond to spells and abilities normally
- Fast effects still use the stack
- Timing restrictions still apply (sorcery speed, etc.)

### 2.6 Zone Transition Handling

#### 2.6.1 Standard Zone Transitions
**Maintained Transitions:**
- Hand to battlefield (casting/playing)
- Battlefield to graveyard (destruction/death)
- Hand to graveyard (discarding)
- Any zone to exile
- Battlefield to hand (bouncing)

**Library-Related Transitions:**
- Hand/battlefield/graveyard to library (creates library if empty)
- Library to hand (draw effects, when library exists)
- Library to graveyard (mill effects, when library exists)
- Library to exile (exile from library effects, when library exists)

#### 2.6.2 Replacement Effects
**Zone Change Replacements:**
- "Instead of being put into graveyard" effects work normally
- "Instead of being drawn" effects work with draw attempts
- "Instead of going to library" effects work normally
- Replacement effects chain normally

### 2.7 Memory and Performance Considerations

#### 2.7.1 Game State Storage
**Minimal State Requirements:**
- With only 6 cards total maximum in game, memory footprint is minimal
- Full game state history can be maintained without performance concerns
- Complete decision tree can be calculated and stored

**State Compression:**
- Game states can be efficiently hashed for duplicate detection
- Minimal memory required for game state representation
- Fast comparison between game states

#### 2.7.2 Performance Optimizations
**Fast State Transitions:**
- Limited card pool means faster rule lookups
- Smaller game state means faster state validation
- Complete game tree fits in memory for most scenarios