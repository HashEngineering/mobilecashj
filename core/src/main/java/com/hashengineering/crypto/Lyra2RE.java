package com.hashengineering.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.cryptohash.BLAKE256;
import fr.cryptohash.BMW256;
import fr.cryptohash.CubeHash256;
import fr.cryptohash.Keccak256;
import fr.cryptohash.Skein256;

/**
 * Created by hashengineering on 9/13/15.
 */
public class Lyra2RE {
    private static boolean nativeLibraryLoaded = false;
    private static final Logger log = LoggerFactory.getLogger(Lyra2RE.class);
    static {
        try {
            System.loadLibrary("diffhash");
            nativeLibraryLoaded = true;
            log.info("Native Libary \"diffhash\" loaded");
        }
        catch(UnsatisfiedLinkError x)
        {

        }
        catch(Exception x)
        {

        }
    }

    public static byte[] digest(byte [] input)
    {
        if(nativeLibraryLoaded)
            return digest_native(input);
        else return digest_java(input);

        /*
        long start2 = System.currentTimeMillis();
        byte[] result = digest_native(input);
        long end2 = System.currentTimeMillis();

        long start1 = System.currentTimeMillis();
        byte[] result2 = digest_java(input);
        long end1 = System.currentTimeMillis();

        log.info("--Lyra2RE: java:  " + (end1-start1) + "ms vs native:  "+ (end2-start2) +"ms");

        return result;
        */
    }
    static native byte[] digest_native(byte [] input);

    public static byte[] digest_java(byte[] input) {
        byte[] digestA;
        byte[] digestB;
        try {
            BLAKE256 blake256 = new BLAKE256();
            digestA = blake256.digest(input);

            Keccak256 keccak = new Keccak256();
            digestB = keccak.digest(digestA);

            CubeHash256 cubehash = new CubeHash256();
            digestA = cubehash.digest(digestB);

            fr.cryptohash.Lyra2.LYRA2(digestB, digestA, digestA, 1, 4, 4);

            Skein256 skein = new Skein256();
            digestA = skein.digest(digestB);

            CubeHash256 cubehash2 = new CubeHash256();
            digestB = cubehash2.digest(digestA);

            BMW256 bmw = new BMW256();
            digestA = bmw.digest(digestB);

            //BMW256 bmw2 = new BMW256();

            return digestA;

        } catch (Exception e) {
            return null;
        }
    }
}
