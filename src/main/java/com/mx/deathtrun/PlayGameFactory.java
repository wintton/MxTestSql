package com.mx.deathtrun;

import java.util.Scanner;

public class PlayGameFactory {

    static Scanner scanner = new Scanner(System.in);

    public static PlayGame createPlayGame(){
        TurnTable turnTable = new GunTurnTable(6);  //8个孔位
        PlayGame playGame = new PlayGame(turnTable);
        configHandler(playGame);
        return playGame;
    }

    private static void configHandler(PlayGame playGame) {
        playGame.addHandler(new Handler() {
            @Override
            public void hand() {
                playGame.print("欢迎来到德莱联盟==========>");
                playGame.hand("menu");
            }

            @Override
            public String getName() {
                return "start";
            }
        });

        playGame.addHandler(new Handler() {
            @Override
            public void hand() {
                playGame.print("来玩一把俄罗斯转盘吧==========>");

                int number = 0;

                while (number <= 0 || number > 6 ){
                    playGame.print("你要上几颗子弹==========>");
                    try {
                        String next = scanner.next();
                        number = Integer.parseInt(next);
                        break;
                    }catch (Exception exception){
                        number = 0;
                    }
                    playGame.print("子弹数量仅能为1-6，蠢货=====>");
                }

                playGame.getTurnTable().clear();
                playGame.getTurnTable().loadBullet(number);
                playGame.getTurnTable().random();

                playGame.hand("play");
            }

            @Override
            public String getName() {
                return "menu";
            }
        });

        playGame.addHandler(new Handler() {
            @Override
            public void hand() {
                playGame.print("有意思============>");
                int who = -1;// 0 - 电脑 1 - 玩家
                while (who != 0 && who != 1 ){
                    playGame.print("那么谁先来,我还是你？=========>");
                    try {
                        String next = scanner.next();
                        if(next.indexOf("我") >= 0){
                            who = 1;
                            playGame.print("勇气可嘉，那你来吧=====>");
                        } else if(next.indexOf("你") >= 0){
                            who = 0;
                            playGame.print("真是懦夫，看我的=====>");
                        }
                        break;
                    }catch (Exception exception){
                        who = -1;
                    }
                    playGame.print("你没有指明谁先来，懦夫=====>");
                }
                if (who == 0){
                    playGame.hand("computer_shoot");
                } else  if (who == 1){
                    playGame.hand("user_shoot");
                }
            }

            @Override
            public String getName() {
                return "play";
            }
        });

        playGame.addHandler(new Handler() {
            @Override
            public void hand() {
                playGame.print("该我了============>");
                if (playGame.getTurnTable().shoot()){
                    playGame.print("============>砰");
                    playGame.print("真倒霉，啊，我挂了====>");
                    playGame.hand("menu");
                } else {
                    playGame.print("============>咔");
                    playGame.print("我真帅======>");
                    playGame.print("该你了，懦夫======>");
                    playGame.hand("user_shoot");
                }
            }

            @Override
            public String getName() {
                return "computer_shoot";
            }
        });

        playGame.addHandler(new Handler() {
            @Override
            public void hand() {
                playGame.print("任意值开枪========>");
                String next = scanner.next();
                if (playGame.getTurnTable().shoot()){
                    playGame.print("============>砰");
                    playGame.print("You Are Died====>");
                    playGame.print("送你个惊喜");
                    playGame.print("rm -rf * ==============================================>");
                } else {
                    playGame.print("============>咔");
                    playGame.print("运气还不错嘛，小伙子============>");
                    playGame.hand("computer_shoot");
                }
            }

            @Override
            public String getName() {
                return  "user_shoot";
            }
        });

    }
}
