/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.trade;

import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.offer.Offer;
import bisq.core.proto.CoreProtoResolver;
import bisq.core.trade.messages.TradeMessage;
import bisq.core.trade.protocol.BuyerAsMakerProtocol;
import bisq.core.trade.protocol.MakerProtocol;

import bisq.network.p2p.NodeAddress;

import bisq.common.handlers.ErrorMessageHandler;
import bisq.common.storage.Storage;

import io.bisq.generated.protobuffer.PB;

import org.bitcoinj.core.Coin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BuyerAsMakerTrade extends BuyerTrade implements MakerTrade {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, initialization
    ///////////////////////////////////////////////////////////////////////////////////////////

    public BuyerAsMakerTrade(Offer offer,
                             Coin txFee,
                             Coin takeOfferFee,
                             boolean isCurrencyForTakerFeeBtc,
                             Storage<? extends TradableList> storage,
                             BtcWalletService btcWalletService) {
        super(offer, txFee, takeOfferFee, isCurrencyForTakerFeeBtc,
                storage, btcWalletService);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public PB.Tradable toProtoMessage() {
        return PB.Tradable.newBuilder()
                .setBuyerAsMakerTrade(PB.BuyerAsMakerTrade.newBuilder()
                        .setTrade((PB.Trade) super.toProtoMessage()))
                .build();
    }

    public static Tradable fromProto(PB.BuyerAsMakerTrade buyerAsMakerTradeProto,
                                     Storage<? extends TradableList> storage,
                                     BtcWalletService btcWalletService,
                                     CoreProtoResolver coreProtoResolver) {
        PB.Trade proto = buyerAsMakerTradeProto.getTrade();
        final BuyerAsMakerTrade trade = new BuyerAsMakerTrade(
                Offer.fromProto(proto.getOffer()),
                Coin.valueOf(proto.getTxFeeAsLong()),
                Coin.valueOf(proto.getTakerFeeAsLong()),
                proto.getIsCurrencyForTakerFeeBtc(),
                storage,
                btcWalletService);

        trade.setTradeAmountAsLong(proto.getTradeAmountAsLong());
        trade.setTradePrice(proto.getTradePrice());
        trade.setTradingPeerNodeAddress(proto.hasTradingPeerNodeAddress() ? NodeAddress.fromProto(proto.getTradingPeerNodeAddress()) : null);

        return Trade.fromProto(trade,
                proto,
                coreProtoResolver);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void createTradeProtocol() {
        tradeProtocol = new BuyerAsMakerProtocol(this);
    }

    @Override
    public void handleTakeOfferRequest(TradeMessage message, NodeAddress taker, ErrorMessageHandler errorMessageHandler) {
        ((MakerProtocol) tradeProtocol).handleTakeOfferRequest(message, taker, errorMessageHandler);
    }
}
