package cn.graht.model.sms.pojos;

import java.util.List;

public class LocationData {
    private Location location;
    private String address;
    private AddressComponent address_component;
    private AdInfo ad_info;
    private AddressReference address_reference;
    private FormattedAddresses formatted_addresses;

    // Getters and Setters
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public AddressComponent getAddress_component() { return address_component; }
    public void setAddress_component(AddressComponent address_component) { this.address_component = address_component; }

    public AdInfo getAd_info() { return ad_info; }
    public void setAd_info(AdInfo ad_info) { this.ad_info = ad_info; }

    public AddressReference getAddress_reference() { return address_reference; }
    public void setAddress_reference(AddressReference address_reference) { this.address_reference = address_reference; }

    public FormattedAddresses getFormatted_addresses() { return formatted_addresses; }
    public void setFormatted_addresses(FormattedAddresses formatted_addresses) { this.formatted_addresses = formatted_addresses; }

    public static class Location {
        private double lat;
        private double lng;

        // Getters and Setters
        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }

    public static class AddressComponent {
        private String nation;
        private String province;
        private String city;
        private String district;
        private String street;
        private String street_number;

        // Getters and Setters
        public String getNation() { return nation; }
        public void setNation(String nation) { this.nation = nation; }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getStreet_number() { return street_number; }
        public void setStreet_number(String street_number) { this.street_number = street_number; }
    }

    public static class AdInfo {
        private String nation_code;
        private String adcode;
        private String phone_area_code;
        private String city_code;
        private String name;
        private Location location;
        private String nation;
        private String province;
        private String city;
        private String district;
        private int _distance;

        // Getters and Setters
        public String getNation_code() { return nation_code; }
        public void setNation_code(String nation_code) { this.nation_code = nation_code; }

        public String getAdcode() { return adcode; }
        public void setAdcode(String adcode) { this.adcode = adcode; }

        public String getPhone_area_code() { return phone_area_code; }
        public void setPhone_area_code(String phone_area_code) { this.phone_area_code = phone_area_code; }

        public String getCity_code() { return city_code; }
        public void setCity_code(String city_code) { this.city_code = city_code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }

        public String getNation() { return nation; }
        public void setNation(String nation) { this.nation = nation; }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }

        public int get_distance() { return _distance; }
        public void set_distance(int _distance) { this._distance = _distance; }
    }

    public static class AddressReference {
        private Town town;
        private LandmarkL2 landmark_l2;
        private Street street;
        private StreetNumber street_number;

        // Getters and Setters
        public Town getTown() { return town; }
        public void setTown(Town town) { this.town = town; }

        public LandmarkL2 getLandmark_l2() { return landmark_l2; }
        public void setLandmark_l2(LandmarkL2 landmark_l2) { this.landmark_l2 = landmark_l2; }

        public Street getStreet() { return street; }
        public void setStreet(Street street) { this.street = street; }

        public StreetNumber getStreet_number() { return street_number; }
        public void setStreet_number(StreetNumber street_number) { this.street_number = street_number; }

        public static class Town {
            private String id;
            private String title;
            private Location location;
            private int _distance;
            private String _dir_desc;

            // Getters and Setters
            public String getId() { return id; }
            public void setId(String id) { this.id = id; }

            public String getTitle() { return title; }
            public void setTitle(String title) { this.title = title; }

            public Location getLocation() { return location; }
            public void setLocation(Location location) { this.location = location; }

            public int get_distance() { return _distance; }
            public void set_distance(int _distance) { this._distance = _distance; }

            public String get_dir_desc() { return _dir_desc; }
            public void set_dir_desc(String _dir_desc) { this._dir_desc = _dir_desc; }
        }

        public static class LandmarkL2 {
            private String id;
            private String title;
            private Location location;
            private int _distance;
            private String _dir_desc;

            // Getters and Setters
            public String getId() { return id; }
            public void setId(String id) { this.id = id; }

            public String getTitle() { return title; }
            public void setTitle(String title) { this.title = title; }

            public Location getLocation() { return location; }
            public void setLocation(Location location) { this.location = location; }

            public int get_distance() { return _distance; }
            public void set_distance(int _distance) { this._distance = _distance; }

            public String get_dir_desc() { return _dir_desc; }
            public void set_dir_desc(String _dir_desc) { this._dir_desc = _dir_desc; }
        }

        public static class Street {
            private String id;
            private String title;
            private Location location;
            private double _distance;
            private String _dir_desc;

            // Getters and Setters
            public String getId() { return id; }
            public void setId(String id) { this.id = id; }

            public String getTitle() { return title; }
            public void setTitle(String title) { this.title = title; }

            public Location getLocation() { return location; }
            public void setLocation(Location location) { this.location = location; }

            public double get_distance() { return _distance; }
            public void set_distance(double _distance) { this._distance = _distance; }

            public String get_dir_desc() { return _dir_desc; }
            public void set_dir_desc(String _dir_desc) { this._dir_desc = _dir_desc; }
        }

        public static class StreetNumber {
            private String id;
            private String title;
            private Location location;
            private double _distance;
            private String _dir_desc;

            // Getters and Setters
            public String getId() { return id; }
            public void setId(String id) { this.id = id; }

            public String getTitle() { return title; }
            public void setTitle(String title) { this.title = title; }

            public Location getLocation() { return location; }
            public void setLocation(Location location) { this.location = location; }

            public double get_distance() { return _distance; }
            public void set_distance(double _distance) { this._distance = _distance; }

            public String get_dir_desc() { return _dir_desc; }
            public void set_dir_desc(String _dir_desc) { this._dir_desc = _dir_desc; }
        }
    }

    public static class FormattedAddresses {
        private String recommend;
        private String rough;
        private String standard_address;

        // Getters and Setters
        public String getRecommend() { return recommend; }
        public void setRecommend(String recommend) { this.recommend = recommend; }

        public String getRough() { return rough; }
        public void setRough(String rough) { this.rough = rough; }

        public String getStandard_address() { return standard_address; }
        public void setStandard_address(String standard_address) { this.standard_address = standard_address; }
    }
}



