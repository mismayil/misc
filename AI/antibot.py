from pdbot import PDBot
import random

COOP = "give 2"
DEF = "take 1"

# strategies
AC = 0
AD = 1
RAND = 2
TFT = 3
GRIM = 4
PAVLOV = 5
TFTT = 6
TTFT = 7
GRADUAL = 8
SM = 9
HM = 10
NP = 11
RP = 12
SGRIM = 13
PROBER = 14
FBF = 15
GTFT = 16
STFT = 17
HTFT = 18
CTFT = 19
RTFT = 20
ATFT = 21
ADAPTIVE = 22
HANDSHAKE = 23
FORTRESS3 = 24
FORTRESS4 = 25
CS = 26

DETECTIVE_PLAYS = [COOP, COOP, DEF, DEF, COOP, DEF, COOP, DEF, COOP, COOP, DEF, DEF, DEF, DEF, DEF, COOP, COOP, DEF]
AC_PATTERN = 'CCCCCCCCCCCCCCCCCC'
AD_PATTERN = 'DDDDDDDDDDDDDDDDDD'
RAND_PATTERN = 'CDCDCDCDCDCDCDCDCD'
TFT_PATTERN = 'CCCDDCDCDCCDDDDDCC'
GRIM_PATTERN = 'CCCDDDDDDDDDDDDDDD'
PAVLOV_PATTERN = 'CCCDCCDDCCCDCDCDDD'
TFTT_PATTERN = 'CCCCDCCCCCCCDCDCCC'
TTFT_PATTERN = 'CCCDDDDDDDCDDDDDDC'
GRADUAL_PATTERN = 'CCCDCCDDCCCDDDCCCC'
SM_PATTERN = 'CCCCCCCCCCCCCDDDDD'
HM_PATTERN = 'DCCCCCCCCCCCCDDDDD'
SGRIM_PATTERN = 'CCCDDDCCDDDCCDDDCC'
PROBER_PATTERN = 'DCCDDCDCDCCDDDDDCC'
FBF_PATTERN = 'CCCDCCDCDCCDCDCDCC'
STFT_PATTERN = 'DCCDDCDCDCCDDDDDCC'
RTFT_PATTERN = 'DDDCCDCDCDDCCCCCDD'
ADAPTIVE_PATTERN = 'CCCCCCDDDDDCDCDCDC'
HANDSHAKE_PATTERN = 'DCDDDDDDDDDDDDDDDD'
FORTRESS3_PATTERN = 'DDCDCDDDDDDDCDCCDD'
FORTRESS4_PATTERN = 'DDDCDDDDDDDDDCCCDD'
CS_PATTERN = 'CDDDDDDDDDDDDDDDDD'

# detect the opponent's strategy
def detect(op_plays):
    num_plays = len(op_plays)
    op_pattern = ''

    for i in xrange(num_plays):
        if op_plays[i] == COOP: op_pattern += 'C'
        else: op_pattern += 'D'

    if op_pattern.find(AC_PATTERN[:num_plays]) != -1: return AC
    if op_pattern.find(AD_PATTERN[:num_plays]) != -1: return AD
    if op_pattern.find(TFT_PATTERN[:num_plays]) != -1: return TFT
    if op_pattern.find(GRIM_PATTERN[:num_plays]) != -1: return GRIM
    if op_pattern.find(PAVLOV_PATTERN[:num_plays]) != -1: return PAVLOV
    if op_pattern.find(TFTT_PATTERN[:num_plays]) != -1: return TFTT
    if op_pattern.find(TTFT_PATTERN[:num_plays]) != -1: return TTFT
    if op_pattern.find(GRADUAL_PATTERN[:num_plays]) != -1: return GRADUAL
    if op_pattern.find(SM_PATTERN[:num_plays]) != -1: return SM
    if op_pattern.find(HM_PATTERN[:num_plays]) != -1: return HM
    if op_pattern.find(SGRIM_PATTERN[:num_plays]) != -1: return SGRIM
    if op_pattern.find(PROBER_PATTERN[:num_plays]) != -1: return PROBER
    if op_pattern.find(FBF_PATTERN[:num_plays]) != -1: return FBF
    if op_pattern.find(STFT_PATTERN[:num_plays]) != -1: return STFT
    if op_pattern.find(RTFT_PATTERN[:num_plays]) != -1: return RTFT
    if op_pattern.find(ADAPTIVE_PATTERN[:num_plays]) != -1: return ADAPTIVE
    if op_pattern.find(HANDSHAKE_PATTERN[:num_plays]) != -1: return HANDSHAKE
    if op_pattern.find(FORTRESS3_PATTERN[:num_plays]) != -1: return FORTRESS3
    if op_pattern.find(FORTRESS4_PATTERN[:num_plays]) != -1: return FORTRESS4
    if op_pattern.find(CS_PATTERN[:num_plays]) != -1: return CS

    return RAND

# match opponent's strategy with a counter strategy
def match(op_strategy):
    if op_strategy == AC: return AD
    if op_strategy == RAND: return TFT
    if op_strategy == GRADUAL: return PAVLOV
    if op_strategy == SGRIM: return GRIM
    if op_strategy == PROBER: return TFT
    if op_strategy == FBF: return PAVLOV
    if op_strategy == RTFT: return TFT
    if op_strategy == STFT: return TFT
    if op_strategy == ADAPTIVE: return PAVLOV
    if op_strategy == HANDSHAKE: return AD
    if op_strategy == FORTRESS3: return TFT
    if op_strategy == FORTRESS4: return PAVLOV
    if op_strategy == CS: return AD

    return op_strategy

class Antibot(PDBot):
    def __init__(self):
        self.games = 0
        self.op_plays = []
        self.op_strategy = TFT
        self.my_strategy = TFT
        self.other_last_play = DEF
        self.my_last_play = DEF

    def init(self):
        self.games += 1
        if self.games == 2:
            self.op_strategy = detect(self.op_plays)
            self.my_strategy = match(self.op_strategy)
        self.op_plays = []
        self.index = 0

    def get_play(self):

        myplay = self.other_last_play

        if self.games == 1:
            if self.index < len(DETECTIVE_PLAYS): myplay = DETECTIVE_PLAYS[self.index]
            self.index += 1

        elif self.my_strategy == AD:
            myplay = DEF

        elif self.my_strategy == TFT:
            myplay = self.other_last_play

        elif self.my_strategy == GRIM:
            if self.other_last_play == COOP:
                myplay = COOP
            else:
                 self.my_strategy = AD
                 myplay = DEF

        elif self.my_strategy == PAVLOV:
            if self.other_last_play == COOP:
                myplay = self.my_last_play
            else:
                if self.my_last_play == COOP: myplay = DEF
                else: myplay = COOP

        elif self.my_strategy == TFTT:
            if len(self.op_plays) > 1 and self.op_plays[-2] == DEF and self.op_plays[-1] == DEF: myplay = DEF
            else: myplay = self.other_last_play

        elif self.my_strategy == TTFT:
            if len(self.op_plays) > 1 and self.op_plays[-2] == DEF: myplay = DEF
            else: myplay = self.other_last_play

        elif self.my_strategy == SM:
            coops = 0
            defs = 0
            for play in self.op_plays:
                if play == COOP: coops += 1
                else: defs += 1
            if coops >= defs: myplay = COOP
            else: myplay = DEF

        elif self.my_strategy == HM:
            coops = 0
            defs = 0
            for play in self.op_plays:
                if play == COOP: coops += 1
                else: defs += 1
            if defs >= coops: myplay = DEF
            else: myplay = COOP

        self.my_last_play = myplay

        return myplay

    def make_play(self,opponent_play):
        self.other_last_play = opponent_play
        self.op_plays.append(opponent_play)
        return
