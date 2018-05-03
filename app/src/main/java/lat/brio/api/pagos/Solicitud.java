package lat.brio.api.pagos;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Delgadillo on 31/07/17.
 */

public class Solicitud extends JSONObject {

    // Datos de comercio
    private long idComercio;
    private String idDispositivo;
    private String token;
    private int tipo;
    private double monto;

    // Parametros de decripción
    private String TLV;
    private String bdk;
    private String PAN;
    private String cardDataMacAlgorithmIdAndMacDOL;
    private String cardholderName;
    private String data;
    private String encTrack1;
    private String encTrack2;
    private String encTrack3;
    private String encTracks;
    private String encWorkingKey;
    private String expiryDate;
    private String finalMessage;
    private String firstSubsidiaryAccountNumber;
    private int formatID;
    private String ksn;
    private String mac;
    private String maskedPAN;
    private String partialTrack;
    private String posEntryMode;
    private String productType;
    private String randomNumber;
    private String secondSubsidiaryAccountNumber;
    private String serialNumber;
    private String serviceCode;
    private String track1Length;
    private String track1Status;
    private String track2Length;
    private String track2Status;
    private String track3Length;
    private String track3Status;
    private String trackEncoding;

    public Solicitud(long idComercio, String idDispositivo, String token, int tipo, double monto, String TLV, String bdk, String PAN, String cardDataMacAlgorithmIdAndMacDOL, String cardholderName, String data, String encTrack1, String encTrack2, String encTrack3, String encTracks, String encWorkingKey, String expiryDate, String finalMessage, String firstSubsidiaryAccountNumber, int formatID, String ksn, String mac, String maskedPAN, String partialTrack, String posEntryMode, String productType, String randomNumber, String secondSubsidiaryAccountNumber, String serialNumber, String serviceCode, String track1Length, String track1Status, String track2Length, String track2Status, String track3Length, String track3Status, String trackEncoding) {
        super();
        this.setIdComercio(idComercio);
        this.setIdDispositivo(idDispositivo);
        this.setToken(token);
        this.setTipo(tipo);
        this.setMonto(monto);
        this.setTLV(TLV);
        this.setBdk(bdk);
        this.setPAN(PAN);
        this.setCardDataMacAlgorithmIdAndMacDOL(cardDataMacAlgorithmIdAndMacDOL);
        this.setCardholderName(cardholderName);
        this.setData(data);
        this.setEncTrack1(encTrack1);
        this.setEncTrack2(encTrack2);
        this.setEncTrack3(encTrack3);
        this.setEncTracks(encTracks);
        this.setEncWorkingKey(encWorkingKey);
        this.setExpiryDate(expiryDate);
        this.setFinalMessage(finalMessage);
        this.setFirstSubsidiaryAccountNumber(firstSubsidiaryAccountNumber);
        this.setFormatID(formatID);
        this.setKsn(ksn);
        this.setMac(mac);
        this.setMaskedPAN(maskedPAN);
        this.setPartialTrack(partialTrack);
        this.setPosEntryMode(posEntryMode);
        this.setProductType(productType);
        this.setRandomNumber(randomNumber);
        this.setSecondSubsidiaryAccountNumber(secondSubsidiaryAccountNumber);
        this.setSerialNumber(serialNumber);
        this.setServiceCode(serviceCode);
        this.setTrack1Length(track1Length);
        this.setTrack1Status(track1Status);
        this.setTrack2Length(track2Length);
        this.setTrack2Status(track2Status);
        this.setTrack3Length(track3Length);
        this.setTrack3Status(track3Status);
        this.setTrackEncoding(trackEncoding);
    }

    public Solicitud(){
        super();
    }

    public long getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(long idComercio) {
        this.idComercio = idComercio;
        try {
            accumulate("idComercio", idComercio);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        try {
            accumulate("token", token);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
        try {
            accumulate("tipo", tipo);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
        try {
            accumulate("monto", monto);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getBdk() {
        return bdk;
    }

    public void setBdk(String bdk) {
        this.bdk = bdk;
        try {
            accumulate("bdk", bdk);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getPAN() {
        return PAN;
    }

    public void setPAN(String PAN) {
        PAN = PAN;
        try {
            accumulate("PAN", PAN);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getCardDataMacAlgorithmIdAndMacDOL() {
        return cardDataMacAlgorithmIdAndMacDOL;
    }

    public void setCardDataMacAlgorithmIdAndMacDOL(String cardDataMacAlgorithmIdAndMacDOL) {
        this.cardDataMacAlgorithmIdAndMacDOL = cardDataMacAlgorithmIdAndMacDOL;
        try {
            accumulate("cardDataMacAlgorithmIdAndMacDOL", cardDataMacAlgorithmIdAndMacDOL);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
        try {
            accumulate("cardholderName", cardholderName);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        try {
            accumulate("data", data);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getEncTrack1() {
        return encTrack1;
    }

    public void setEncTrack1(String encTrack1) {
        this.encTrack1 = encTrack1;
        try {
            accumulate("encTrack1", encTrack1);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getEncTrack2() {
        return encTrack2;
    }

    public void setEncTrack2(String encTrack2) {
        this.encTrack2 = encTrack2;
        try {
            accumulate("encTrack2", encTrack2);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getEncTrack3() {
        return encTrack3;
    }

    public void setEncTrack3(String encTrack3) {
        this.encTrack3 = encTrack3;
        try {
            accumulate("encTrack3", encTrack3);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getEncTracks() {
        return encTracks;
    }

    public void setEncTracks(String encTracks) {
        this.encTracks = encTracks;
        try {
            accumulate("encTracks", encTracks);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getEncWorkingKey() {
        return encWorkingKey;
    }

    public void setEncWorkingKey(String encWorkingKey) {
        this.encWorkingKey = encWorkingKey;
        try {
            accumulate("encWorkingKey", encWorkingKey);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
        try {
            accumulate("expiryDate", expiryDate);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getFinalMessage() {
        return finalMessage;
    }

    public void setFinalMessage(String finalMessage) {
        this.finalMessage = finalMessage;
        try {
            accumulate("finalMessage", finalMessage);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getFirstSubsidiaryAccountNumber() {
        return firstSubsidiaryAccountNumber;
    }

    public void setFirstSubsidiaryAccountNumber(String firstSubsidiaryAccountNumber) {
        this.firstSubsidiaryAccountNumber = firstSubsidiaryAccountNumber;
        try {
            accumulate("firstSubsidiaryAccountNumber", firstSubsidiaryAccountNumber);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public int getFormatID() {
        return formatID;
    }

    public void setFormatID(int formatID) {
        this.formatID = formatID;
        try {
            accumulate("formatID", formatID);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getKsn() {
        return ksn;
    }

    public void setKsn(String ksn) {
        this.ksn = ksn;
        try {
            accumulate("ksn", ksn);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
        try {
            accumulate("mac", mac);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getMaskedPAN() {
        return maskedPAN;
    }

    public void setMaskedPAN(String maskedPAN) {
        this.maskedPAN = maskedPAN;
        try {
            accumulate("maskedPAN", maskedPAN);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getPartialTrack() {
        return partialTrack;
    }

    public void setPartialTrack(String partialTrack) {
        this.partialTrack = partialTrack;
        try {
            accumulate("partialTrack", partialTrack);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getPosEntryMode() {
        return posEntryMode;
    }

    public void setPosEntryMode(String posEntryMode) {
        this.posEntryMode = posEntryMode;
        try {
            accumulate("posEntryMode", posEntryMode);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
        try {
            accumulate("productType", productType);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
        try {
            accumulate("randomNumber", randomNumber);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getSecondSubsidiaryAccountNumber() {
        return secondSubsidiaryAccountNumber;
    }

    public void setSecondSubsidiaryAccountNumber(String secondSubsidiaryAccountNumber) {
        this.secondSubsidiaryAccountNumber = secondSubsidiaryAccountNumber;
        try {
            accumulate("secondSubsidiaryAccountNumber", secondSubsidiaryAccountNumber);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        try {
            accumulate("serialNumber", serialNumber);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        try {
            accumulate("serviceCode", serviceCode);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTrack1Length() {
        return track1Length;
    }

    public void setTrack1Length(String track1Length) {
        this.track1Length = track1Length;
        try {
            accumulate("track1Length", track1Length);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTrack1Status() {
        return track1Status;
    }

    public void setTrack1Status(String track1Status) {
        this.track1Status = track1Status;
        try {
            accumulate("track1Status", track1Status);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTrack2Length() {
        return track2Length;
    }

    public void setTrack2Length(String track2Length) {
        this.track2Length = track2Length;
        try {
            accumulate("track2Length", track2Length);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTrack2Status() {
        return track2Status;
    }

    public void setTrack2Status(String track2Status) {
        this.track2Status = track2Status;
        try {
            accumulate("track2Status", track2Status);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTrack3Length() {
        return track3Length;
    }

    public void setTrack3Length(String track3Length) {
        this.track3Length = track3Length;
        try {
            accumulate("track3Length", track3Length);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTrack3Status() {
        return track3Status;
    }

    public void setTrack3Status(String track3Status) {
        this.track3Status = track3Status;
        try {
            accumulate("track3Status", track3Status);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTrackEncoding() {
        return trackEncoding;
    }

    public void setTrackEncoding(String trackEncoding) {
        this.trackEncoding = trackEncoding;
        try {
            accumulate("trackEncoding", trackEncoding);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getTLV() {
        return TLV;
    }

    public void setTLV(String TLV) {
        TLV = TLV;
        try {
            accumulate("TLV", TLV);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
        try {
            accumulate("idDispositivo", idDispositivo);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }
}
