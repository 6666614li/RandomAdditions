package com.lw.random_additons.common.handler;

import com.lw.random_additons.cilent.handler.WirelessInput;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    private static int packetId = 0;
    public static final SimpleNetworkWrapper WirelessDeposit = NetworkRegistry.INSTANCE.newSimpleChannel("ae2top");

    public static void registerPackets() {
        WirelessDeposit.registerMessage(
                WirelessInput.Handler.class,
                WirelessInput.class,
                packetId++,
                Side.SERVER
        );
    }
}

