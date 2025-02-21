package com.modsen.driver.util;

import java.util.UUID;

public class TestUtils {
    public static final String ACTIVE_PARAM = "active";
    public static final Long NON_EXISTING_ID = 999L;
    public static final Long EXISTING_ID = 10L;
    public static final Long EDIT_ID = 11L;

    public static final UUID NON_EXISTING_DRIVER_ID = UUID.fromString("11111111-9999-1111-1111-111111111111");
    public static final UUID EXISTING_DRIVER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID EDIT_DRIVER_ID = UUID.fromString("11111111-0000-1111-1111-111111111111");

    public static final String DRIVER_BASE_URL = "/api/v1/drivers";
    public static final String DRIVER_BY_ID_URL = DRIVER_BASE_URL + "/{id}";
    public static final String DRIVER_EXISTS_URL = DRIVER_BY_ID_URL + "/exists";

    public static final String CAR_BASE_URL = "/api/v1/cars";
    public static final String CAR_BY_ID_URL = CAR_BASE_URL + "/{id}";

    public static final String TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3VVNlNUYwQTNsMXFMd0hYSVFlUXozc2dQLXdiUVFCUUxaR3B2Zk5ZV3Q0In0.eyJleHAiOjE3Mzk3OTE2NjksImlhdCI6MTczOTc4OTg3MCwianRpIjoiNmRmNTBiNGUtNGIyMC00Njk5LTg3YzMtNmE1ZDJkYzczZGFjIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3RheGkiLCJhdWQiOlsiYXV0aC1zZXJ2aWNlIiwiYWNjb3VudCJdLCJzdWIiOiI2NmE1Njk1OC03NGRlLTRmMDctYTMyNi1iMDRkMDdiZDk2ZGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhdXRoIiwic2lkIjoiNzY2ZjQ5YWMtYjNmZS00MjUxLTllMWMtNGQ4YjA0MTI3OGJjIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiUk9MRV9vZmZsaW5lX2FjY2VzcyIsIlJPTEVfUEFTU0VOR0VSIiwiUk9MRV9kZWZhdWx0LXJvbGVzLXRheGkiLCJST0xFX3VtYV9hdXRob3JpemF0aW9uIiwiUk9MRV9EUklWRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ0ZXN0IHRlc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJ0ZXN0IiwiZmFtaWx5X25hbWUiOiJ0ZXN0IiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSJ9.Kwvfz9sEgeNecIdJTFHQjIjct-GtssaRRv2G6Llj6gsii8-YcafFIgO7bFQJZOoYnxKCo9bXm1XKCoG-eQLYcKj9AgKfe72a-O6mv-Aen1N31Dg-sO1-6c_bFe6rKBniRl8jR3NaJJWfAtNONevCshbPTDFEoo-qVibCvBE6rW8ipMS80ocZw1wm6gP1HTfaOlHeV_dfyvs3vNxvsS7nziVLv82JUf3Y29BT1uqNSKVCTvDeRnuvyFEW1H5rgT0HEonK8PpICaUP1_--mkL5u0CTQpBrB6pXTyj0ah3MAIgu_jS3QTiT5OD8TtdnL9elmxitgbqsIxQZH7FynsqR5A";
}
