-- =============================================
-- PG MANAGEMENT SYSTEM - DATABASE SCHEMA
-- =============================================
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable PostGIS for geospatial queries (optional)
CREATE EXTENSION IF NOT EXISTS postgis;

-- =============================================
-- 1. USERS & AUTHENTICATION
-- =============================================
CREATE TABLE users (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	email varchar(255) UNIQUE NOT NULL,
	mobile varchar(15) UNIQUE NOT NULL,
	password_hash varchar(255), -- NULL for Google OAuth users
	name varchar(255) NOT NULL,
	profile_picture varchar(500),
	gender varchar(20),
	occupation varchar(100), -- Student, Working Professional, Other
	preferred_locations text[], -- Array of location strings
	budget_min decimal(10, 2),
	budget_max decimal(10, 2),
	move_in_date date,
	is_email_verified boolean DEFAULT FALSE,
	is_mobile_verified boolean DEFAULT FALSE,
	email_verification_token varchar(255),
	email_verification_expires timestamp,
	password_reset_token varchar(255),
	password_reset_expires timestamp,
	mfa_enabled boolean DEFAULT FALSE,
	mfa_secret varchar(255),
	google_id varchar(255) UNIQUE,
	last_login timestamp,
	login_attempts integer DEFAULT 0,
	locked_until timestamp,
	is_blocked boolean DEFAULT FALSE,
	blocked_reason text,
	blocked_at timestamp,
	blocked_by uuid REFERENCES users (id),
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE owners (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	email varchar(255) UNIQUE NOT NULL,
	mobile varchar(15) UNIQUE NOT NULL,
	password_hash varchar(255),
	name varchar(255) NOT NULL,
	profile_picture varchar(500),
	company_name varchar(255),
	is_email_verified boolean DEFAULT FALSE,
	is_mobile_verified boolean DEFAULT FALSE,
	email_verification_token varchar(255),
	email_verification_expires timestamp,
	password_reset_token varchar(255),
	password_reset_expires timestamp,
	mfa_enabled boolean DEFAULT FALSE,
	mfa_secret varchar(255),
	google_id varchar(255) UNIQUE,
	-- Verification
	is_verified boolean DEFAULT FALSE,
	verification_status varchar(50) DEFAULT 'pending', -- pending, approved, rejected
	id_proof_url varchar(500),
	address_proof_url varchar(500),
	bank_account_number varchar(50),
	bank_ifsc_code varchar(20),
	bank_name varchar(255),
	verification_notes text,
	verified_at timestamp,
	verified_by uuid REFERENCES users (id),
	-- Trust Score
	trust_score integer DEFAULT 50, -- 0-100
	response_time_avg integer, -- in minutes
	visit_conversion_rate decimal(5, 2),
	complaint_count integer DEFAULT 0,
	-- Settings
	auto_respond_enabled boolean DEFAULT FALSE,
	auto_respond_message text,
	availability_hours jsonb, -- {monday: {start: "09:00", end: "18:00"}, ...}
	notification_preferences jsonb,
	last_login timestamp,
	login_attempts integer DEFAULT 0,
	locked_until timestamp,
	is_blocked boolean DEFAULT FALSE,
	blocked_reason text,
	blocked_at timestamp,
	blocked_by uuid REFERENCES users (id),
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admins (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	email varchar(255) UNIQUE NOT NULL,
	password_hash varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	role varchar(50) DEFAULT 'admin', -- admin, super_admin
	permissions jsonb, -- Array of permission strings
	last_login timestamp,
	login_attempts integer DEFAULT 0,
	locked_until timestamp,
	is_active boolean DEFAULT TRUE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessions (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	user_id uuid,
	owner_id uuid,
	admin_id uuid,
	user_type varchar(20) NOT NULL, -- user, owner, admin
	token varchar(500) NOT NULL UNIQUE,
	refresh_token varchar(500) UNIQUE,
	ip_address varchar(50),
	user_agent text,
	device_info jsonb,
	expires_at timestamp NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_user CHECK ((user_type = 'user' AND user_id IS NOT NULL) OR (user_type = 'owner' AND owner_id IS NOT NULL) OR (user_type = 'admin' AND admin_id IS NOT NULL))
);

CREATE TABLE password_history (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	user_id uuid,
	owner_id uuid,
	user_type varchar(20) NOT NULL,
	password_hash varchar(255) NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 2. PG PROPERTIES
-- =============================================
CREATE TABLE pgs (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	owner_id uuid NOT NULL REFERENCES owners (id) ON DELETE CASCADE,
	-- Basic Information
	name varchar(255) NOT NULL,
	slug varchar(255) UNIQUE NOT NULL,
	description text NOT NULL,
	summary text,
	property_type varchar(50), -- Independent house, Apartment, Villa
	total_floors integer,
	total_rooms integer,
	establishment_year integer,
	-- Location
	address text NOT NULL,
	city varchar(100) NOT NULL,
	state varchar(100) NOT NULL,
	pincode varchar(10) NOT NULL,
	landmark varchar(255),
	latitude decimal(10, 8),
	longitude decimal(11, 8),
	location GEOGRAPHY (point, 4326), -- PostGIS point for geospatial queries
	nearby_locations jsonb, -- {transport: [], education: [], hospitals: [], shopping: []}
	-- Accommodation
	gender_type varchar(20), -- Male, Female, Unisex
	occupancy_type varchar(50), -- Sharing, Private
	furnishing_type varchar(50), -- Furnished, Semi-furnished, Unfurnished
	security_deposit decimal(10, 2),
	notice_period_days integer,
	-- Food
	food_available boolean DEFAULT FALSE,
	food_type varchar(50), -- Veg, Non-veg, Both
	food_plans jsonb, -- {breakfast: true, lunch: false, dinner: true}
	food_description text,
	food_pricing jsonb, -- {breakfast: 1500, lunch: 2000, dinner: 1800}
	-- Rules & Policies
	house_rules text,
	cancellation_policy text,
	payment_terms text,
	curfew_time time,
	guest_policy text,
	-- Media
	floor_plan_url varchar(500),
	virtual_tour_url varchar(500),
	-- Status
	status varchar(50) DEFAULT 'draft', -- draft, pending, approved, blocked, archived
	approval_status varchar(50) DEFAULT 'pending', -- pending, approved, rejected
	approval_notes text,
	approved_at timestamp,
	approved_by uuid REFERENCES admins (id),
	rejected_reason text,
	is_featured boolean DEFAULT FALSE,
	featured_until timestamp,
	featured_locations text[], -- homepage, city_page, etc.
	-- Metrics
	view_count integer DEFAULT 0,
	favorite_count integer DEFAULT 0,
	contact_click_count integer DEFAULT 0,
	visit_request_count integer DEFAULT 0,
	share_count integer DEFAULT 0,
	-- SEO
	meta_title varchar(255),
	meta_description text,
	is_archived boolean DEFAULT FALSE,
	archived_at timestamp,
	is_deleted boolean DEFAULT FALSE,
	deleted_at timestamp,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pg_rooms (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	room_type varchar(50) NOT NULL, -- Single, Double, Triple, Dormitory
	beds_per_room integer NOT NULL,
	total_rooms integer NOT NULL,
	available_beds integer NOT NULL,
	price_per_bed decimal(10, 2) NOT NULL,
	price_per_month decimal(10, 2) NOT NULL,
	room_size_sqft integer,
	has_attached_bathroom boolean DEFAULT FALSE,
	has_balcony boolean DEFAULT FALSE,
	has_ac boolean DEFAULT FALSE,
	description text,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pg_images (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	image_url varchar(500) NOT NULL,
	thumbnail_url varchar(500),
	image_type varchar(50), -- exterior, room, kitchen, bathroom, common_area, other
	display_order integer DEFAULT 0,
	is_primary boolean DEFAULT FALSE,
	alt_text varchar(255),
	uploaded_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE amenities (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	name varchar(100) NOT NULL UNIQUE,
	category varchar(50), -- basic, safety, entertainment, services
	icon_name varchar(50),
	display_order integer DEFAULT 0,
	is_active boolean DEFAULT TRUE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pg_amenities (
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	amenity_id uuid NOT NULL REFERENCES amenities (id) ON DELETE CASCADE,
	is_paid boolean DEFAULT FALSE,
	price decimal(10, 2),
	notes text,
	PRIMARY KEY (pg_id, amenity_id),
	created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 3. USER INTERACTIONS
-- =============================================
CREATE TABLE favorites (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (user_id, pg_id)
);

CREATE TABLE visits (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
	owner_id uuid NOT NULL REFERENCES owners (id),
	visit_type varchar(50) DEFAULT 'physical', -- physical, virtual
	preferred_date date NOT NULL,
	preferred_time_slot varchar(50) NOT NULL, -- morning, afternoon, evening
	preferred_time time,
	status varchar(50) DEFAULT 'pending', -- pending, accepted, rejected, rescheduled, completed, cancelled
	-- Owner Response
	owner_response varchar(50), -- accepted, rejected, rescheduled
	owner_notes text,
	responded_at timestamp,
	-- Rescheduling
	rescheduled_date date,
	rescheduled_time time,
	rescheduled_reason text,
	-- Cancellation
	cancelled_by varchar(50), -- user, owner
	cancellation_reason text,
	cancelled_at timestamp,
	-- Completion
	completed_at timestamp,
	completion_notes text,
	-- User Notes
	user_notes text,
	special_requirements text,
	-- Communication
	whatsapp_reminder_sent boolean DEFAULT FALSE,
	whatsapp_reminder_sent_at timestamp,
	confirmation_sent boolean DEFAULT FALSE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE callbacks (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
	owner_id uuid NOT NULL REFERENCES owners (id),
	user_name varchar(255) NOT NULL,
	user_mobile varchar(15) NOT NULL,
	preferred_time varchar(100),
	message text,
	status varchar(50) DEFAULT 'pending', -- pending, called, converted, not_interested, expired
	-- Owner Actions
	called_at timestamp,
	call_notes text,
	follow_up_date date,
	follow_up_notes text,
	-- Conversion
	is_converted boolean DEFAULT FALSE,
	converted_at timestamp,
	expires_at timestamp DEFAULT CURRENT_TIMESTAMP + interval '7 days',
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reviews (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
	rating integer NOT NULL CHECK (rating >= 1 AND rating <= 5),
	review_text text,
	-- Detailed Ratings
	cleanliness_rating integer CHECK (cleanliness_rating >= 1 AND cleanliness_rating <= 5),
	food_rating integer CHECK (food_rating >= 1 AND food_rating <= 5),
	facilities_rating integer CHECK (facilities_rating >= 1 AND facilities_rating <= 5),
	location_rating integer CHECK (location_rating >= 1 AND location_rating <= 5),
	value_for_money_rating integer CHECK (value_for_money_rating >= 1 AND value_for_money_rating <= 5),
	-- Status
	is_approved boolean DEFAULT FALSE,
	approved_at timestamp,
	approved_by uuid REFERENCES admins (id),
	-- Owner Response
	owner_response text,
	owner_responded_at timestamp,
	-- Helpful Count
	helpful_count integer DEFAULT 0,
	is_deleted boolean DEFAULT FALSE,
	deleted_at timestamp,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (pg_id, user_id)
);

CREATE TABLE review_helpful (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	review_id uuid NOT NULL REFERENCES reviews (id) ON DELETE CASCADE,
	user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (review_id, user_id)
);

-- =============================================
-- 4. SUBSCRIPTIONS & PAYMENTS
-- =============================================
CREATE TABLE subscription_plans (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	name varchar(100) NOT NULL,
	description text,
	duration_days integer NOT NULL,
	price decimal(10, 2) NOT NULL,
	features jsonb, -- Array of feature strings
	max_listings integer,
	is_featured boolean DEFAULT FALSE,
	display_order integer DEFAULT 0,
	is_active boolean DEFAULT TRUE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subscriptions (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	owner_id uuid NOT NULL REFERENCES owners (id) ON DELETE CASCADE,
	plan_id uuid NOT NULL REFERENCES subscription_plans (id),
	status varchar(50) DEFAULT 'active', -- trial, active, expired, cancelled
	start_date date NOT NULL,
	end_date date NOT NULL,
	auto_renew boolean DEFAULT TRUE,
	price_paid decimal(10, 2) NOT NULL,
	cancelled_at timestamp,
	cancellation_reason text,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	owner_id uuid NOT NULL REFERENCES owners (id) ON DELETE CASCADE,
	subscription_id uuid REFERENCES subscriptions (id),
	amount decimal(10, 2) NOT NULL,
	currency varchar(10) DEFAULT 'INR',
	payment_method varchar(50), -- card, upi, netbanking, wallet
	payment_gateway varchar(50), -- razorpay, stripe, payu
	gateway_transaction_id varchar(255) UNIQUE,
	gateway_order_id varchar(255),
	status varchar(50) DEFAULT 'pending', -- pending, success, failed, refunded
	-- Payment Details
	payment_details jsonb, -- Store gateway response
	-- Refund
	refund_amount decimal(10, 2),
	refund_reason text,
	refunded_at timestamp,
	paid_at timestamp,
	failed_reason text,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoices (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	owner_id uuid NOT NULL REFERENCES owners (id) ON DELETE CASCADE,
	payment_id uuid REFERENCES payments (id),
	subscription_id uuid REFERENCES subscriptions (id),
	invoice_number varchar(50) UNIQUE NOT NULL,
	invoice_date date NOT NULL,
	due_date date,
	amount decimal(10, 2) NOT NULL,
	tax_amount decimal(10, 2) DEFAULT 0,
	total_amount decimal(10, 2) NOT NULL,
	status varchar(50) DEFAULT 'unpaid', -- unpaid, paid, overdue, cancelled
	invoice_url varchar(500),
	paid_at timestamp,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 5. NOTIFICATIONS
-- =============================================
CREATE TABLE notifications (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	recipient_id uuid NOT NULL,
	recipient_type varchar(20) NOT NULL, -- user, owner, admin
	type varchar(50) NOT NULL, -- email, whatsapp, in_app, sms, push
	category varchar(50), -- visit, payment, approval, message
	title varchar(255) NOT NULL,
	message text NOT NULL,
	-- Routing Info
	link_url varchar(500),
	action_text varchar(100),
	-- Delivery Status
	status varchar(50) DEFAULT 'pending', -- pending, sent, delivered, failed, read
	sent_at timestamp,
	delivered_at timestamp,
	read_at timestamp,
	failed_reason text,
	-- Priority
	priority varchar(20) DEFAULT 'normal', -- low, normal, high, urgent
	-- Metadata
	metadata jsonb,
	expires_at timestamp,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification_preferences (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	user_id uuid,
	owner_id uuid,
	user_type varchar(20) NOT NULL,
	email_enabled boolean DEFAULT TRUE,
	sms_enabled boolean DEFAULT TRUE,
	whatsapp_enabled boolean DEFAULT TRUE,
	push_enabled boolean DEFAULT TRUE,
	in_app_enabled boolean DEFAULT TRUE,
	-- Granular Preferences
	visit_notifications boolean DEFAULT TRUE,
	payment_notifications boolean DEFAULT TRUE,
	marketing_notifications boolean DEFAULT FALSE,
	weekly_digest boolean DEFAULT TRUE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_user_type CHECK ((user_type = 'user' AND user_id IS NOT NULL) OR (user_type = 'owner' AND owner_id IS NOT NULL))
);

-- =============================================
-- 6. ANALYTICS & TRACKING
-- =============================================
CREATE TABLE pg_views (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	user_id uuid REFERENCES users (id),
	ip_address varchar(50),
	user_agent text,
	referrer varchar(500),
	session_id varchar(255),
	viewed_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE search_queries (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	user_id uuid REFERENCES users (id),
	query text NOT NULL,
	query_type varchar(50), -- full_text, natural_language, filter_based
	filters jsonb, -- Store applied filters
	results_count integer,
	clicked_pg_id uuid REFERENCES pgs (id),
	click_position integer, -- Position in search results
	session_id varchar(255),
	ip_address varchar(50),
	created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_activity_logs (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	user_id uuid,
	owner_id uuid,
	admin_id uuid,
	user_type varchar(20) NOT NULL,
	activity_type varchar(100) NOT NULL, -- login, logout, pg_create, pg_edit, etc.
	activity_description text,
	entity_type varchar(50), -- pg, user, owner, payment, etc.
	entity_id uuid,
	metadata jsonb, -- Additional context
	ip_address varchar(50),
	user_agent text,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 7. CONTENT MANAGEMENT
-- =============================================
CREATE TABLE blog_posts (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	author_id uuid REFERENCES admins (id),
	title varchar(255) NOT NULL,
	slug varchar(255) UNIQUE NOT NULL,
	content text NOT NULL,
	excerpt text,
	featured_image_url varchar(500),
	status varchar(50) DEFAULT 'draft', -- draft, published, scheduled, archived
	published_at timestamp,
	scheduled_for timestamp,
	-- SEO
	meta_title varchar(255),
	meta_description text,
	meta_keywords text,
	-- Categorization
	categories text[],
	tags text[],
	-- Metrics
	view_count integer DEFAULT 0,
	share_count integer DEFAULT 0,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE blog_comments (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	post_id uuid NOT NULL REFERENCES blog_posts (id) ON DELETE CASCADE,
	user_id uuid REFERENCES users (id),
	comment_text text NOT NULL,
	is_approved boolean DEFAULT FALSE,
	approved_at timestamp,
	approved_by uuid REFERENCES admins (id),
	is_deleted boolean DEFAULT FALSE,
	deleted_at timestamp,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE finest_pgs (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	pg_id uuid NOT NULL REFERENCES pgs (id) ON DELETE CASCADE,
	title varchar(255),
	description text,
	editorial_notes text,
	display_order integer DEFAULT 0,
	added_by uuid REFERENCES admins (id),
	added_at timestamp DEFAULT CURRENT_TIMESTAMP,
	is_active boolean DEFAULT TRUE
);

CREATE TABLE popular_cities (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	city_name varchar(100) NOT NULL UNIQUE,
	state varchar(100) NOT NULL,
	image_url varchar(500),
	description text,
	listing_count integer DEFAULT 0,
	display_order integer DEFAULT 0,
	is_featured boolean DEFAULT TRUE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 8. REPORTS & MODERATION
-- =============================================
CREATE TABLE reports (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	reporter_id uuid REFERENCES users (id) ON DELETE CASCADE,
	entity_type varchar(50) NOT NULL, -- pg, user, owner, review
	entity_id uuid NOT NULL,
	reason varchar(100) NOT NULL,
	description text,
	status varchar(50) DEFAULT 'pending', -- pending, reviewing, resolved, dismissed
	-- Admin Action
	reviewed_by uuid REFERENCES admins (id),
	reviewed_at timestamp,
	admin_notes text,
	action_taken text,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 9. SYSTEM TABLES
-- =============================================
CREATE TABLE system_settings (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	setting_key varchar(100) NOT NULL UNIQUE,
	setting_value text,
	setting_type varchar(50), -- string, number, boolean, json
	description text,
	is_editable boolean DEFAULT TRUE,
	updated_by uuid REFERENCES admins (id),
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE email_templates (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4 (),
	template_name varchar(100) NOT NULL UNIQUE,
	subject varchar(255) NOT NULL,
	body_html text NOT NULL,
	body_text text,
	variables jsonb, -- Array of variable names used in template
	is_active boolean DEFAULT TRUE,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- INDEXES FOR PERFORMANCE
-- =============================================
-- Users & Authentication
CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_users_mobile ON users (mobile);

CREATE INDEX idx_users_google_id ON users (google_id);

CREATE INDEX idx_owners_email ON owners (email);

CREATE INDEX idx_owners_mobile ON owners (mobile);

CREATE INDEX idx_sessions_token ON sessions (token);

CREATE INDEX idx_sessions_user ON sessions (user_id, user_type);

-- PGs
CREATE INDEX idx_pgs_owner ON pgs (owner_id);

CREATE INDEX idx_pgs_status ON pgs (status);

CREATE INDEX idx_pgs_city ON pgs (city);

CREATE INDEX idx_pgs_city_status ON pgs (city, status);

CREATE INDEX idx_pgs_gender_type ON pgs (gender_type);

CREATE INDEX idx_pgs_is_featured ON pgs (is_featured);

CREATE INDEX idx_pgs_slug ON pgs (slug);

-- Full-text search on PGs
CREATE INDEX idx_pgs_search ON pgs USING GIN (to_tsvector('english', coalesce(name, '') || ' ' || coalesce(description, '') || ' ' || coalesce(city, '') || ' ' || coalesce(address, '')));

-- Geospatial index (if using PostGIS)
CREATE INDEX idx_pgs_location ON pgs USING GIST (location);

-- PG Rooms
CREATE INDEX idx_pg_rooms_pg ON pg_rooms (pg_id);

CREATE INDEX idx_pg_rooms_type_price ON pg_rooms (room_type, price_per_month);

-- Interactions
CREATE INDEX idx_favorites_user ON favorites (user_id);

CREATE INDEX idx_favorites_pg ON favorites (pg_id);

CREATE INDEX idx_visits_user ON visits (user_id);

CREATE INDEX idx_visits_owner ON visits (owner_id);

CREATE INDEX idx_visits_pg ON visits (pg_id);

CREATE INDEX idx_visits_status ON visits (status);

CREATE INDEX idx_callbacks_owner_status ON callbacks (owner_id, status);

CREATE INDEX idx_reviews_pg ON reviews (pg_id);

CREATE INDEX idx_reviews_approved ON reviews (is_approved);

-- Subscriptions
CREATE INDEX idx_subscriptions_owner ON subscriptions (owner_id);

CREATE INDEX idx_subscriptions_status ON subscriptions (status);

CREATE INDEX idx_payments_owner ON payments (owner_id);

CREATE INDEX idx_payments_status ON payments (status);

-- Notifications
CREATE INDEX idx_notifications_recipient ON notifications (recipient_id, recipient_type);

CREATE INDEX idx_notifications_status ON notifications (status);

CREATE INDEX idx_notifications_created ON notifications (created_at);

-- Analytics
CREATE INDEX idx_pg_views_pg ON pg_views (pg_id);

CREATE INDEX idx_pg_views_date ON pg_views (viewed_at);

CREATE INDEX idx_search_queries_user ON search_queries (user_id);

CREATE INDEX idx_search_queries_date ON search_queries (created_at);

-- Blogs
CREATE INDEX idx_blog_posts_status ON blog_posts (status);

CREATE INDEX idx_blog_posts_slug ON blog_posts (slug);

CREATE INDEX idx_blog_posts_published ON blog_posts (published_at);

-- =============================================
-- TRIGGERS FOR UPDATED_AT
-- =============================================
CREATE OR REPLACE FUNCTION update_updated_at_column ()
	RETURNS TRIGGER
	AS $$
BEGIN
	NEW.updated_at = CURRENT_TIMESTAMP;
	RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

-- Apply to tables with updated_at
CREATE TRIGGER update_users_updated_at
	BEFORE UPDATE ON users
	FOR EACH ROW
	EXECUTE FUNCTION update_updated_at_column ();

CREATE TRIGGER update_owners_updated_at
	BEFORE UPDATE ON owners
	FOR EACH ROW
	EXECUTE FUNCTION update_updated_at_column ();

CREATE TRIGGER update_pgs_updated_at
	BEFORE UPDATE ON pgs
	FOR EACH ROW
	EXECUTE FUNCTION update_updated_at_column ();

CREATE TRIGGER update_pg_rooms_updated_at
	BEFORE UPDATE ON pg_rooms
	FOR EACH ROW
	EXECUTE FUNCTION update_updated_at_column ();

CREATE TRIGGER update_visits_updated_at
	BEFORE UPDATE ON visits
	FOR EACH ROW
	EXECUTE FUNCTION update_updated_at_column ();

CREATE TRIGGER update_subscriptions_updated_at
	BEFORE UPDATE ON subscriptions
	FOR EACH ROW
	EXECUTE FUNCTION update_updated_at_column ();

CREATE TRIGGER update_blog_posts_updated_at
	BEFORE UPDATE ON blog_posts
	FOR EACH ROW
	EXECUTE FUNCTION update_updated_at_column ();

-- =============================================
-- INITIAL SEED DATA
-- =============================================
-- Insert default amenities
INSERT INTO amenities (name, category, icon_name)
	VALUES ('WiFi', 'basic', 'wifi'), ('AC', 'basic', 'wind'), ('TV', 'entertainment', 'tv'), ('Refrigerator', 'basic', 'refrigerator'), ('Washing Machine', 'services', 'washing-machine'), ('Parking - Two Wheeler', 'basic', 'bike'), ('Parking - Four Wheeler', 'basic', 'car'), ('Power Backup', 'basic', 'zap'), ('24/7 Water Supply', 'basic', 'droplet'), ('Geyser', 'basic', 'thermometer'), ('CCTV', 'safety', 'camera'), ('Security Guard', 'safety', 'shield'), ('Housekeeping', 'services', 'broom'), ('Laundry Service', 'services', 'shirt'), ('Gym', 'entertainment', 'dumbbell'), ('Common Room', 'entertainment', 'users'), ('Garden', 'basic', 'tree'), ('Elevator', 'basic', 'arrow-up'), ('Attached Bathroom', 'basic', 'bath'), ('Balcony', 'basic', 'wind'), ('Dining Hall', 'basic', 'utensils'), ('Study Room', 'entertainment', 'book'), ('RO Water', 'basic', 'droplet'), ('Inverter', 'basic', 'battery'), ('Fire Extinguisher', 'safety', 'fire-extinguisher'), ('First Aid', 'safety', 'heart-pulse');

-- Insert default subscription plans
INSERT INTO subscription_plans (name, description, duration_days, price, features, max_listings, is_featured, display_order)
	VALUES ('Free Trial', '7-day trial for new owners', 7, 0, '["1 PG Listing", "Basic Analytics", "Email Support"]'::jsonb, 1, FALSE, 1), ('Basic', 'Perfect for individual PG owners', 30, 499, '["Up to 3 PG Listings", "Basic Analytics", "Email & Phone Support", "WhatsApp Notifications"]'::jsonb, 3, FALSE, 2), ('Professional', 'Best for small PG businesses', 30, 999, '["Up to 10 PG Listings", "Advanced Analytics", "Priority Support", "WhatsApp & SMS Notifications", "1 Featured Listing/month"]'::jsonb, 10, TRUE, 3), ('Enterprise', 'For large PG chains', 30, 2499, '["Unlimited PG Listings", "Advanced Analytics with Insights", "Dedicated Account Manager", "All Notifications", "5 Featured Listings/month", "API Access"]'::jsonb, 999, FALSE, 4);

-- Insert default email templates
INSERT INTO email_templates (template_name, subject, body_html, variables)
	VALUES ('welcome_user', 'Welcome to PG Finder!', '<h1>Welcome {{name}}!</h1><p>Thank you for joining PG Finder. Start exploring the best PG accommodations near you.</p>', '["name"]'::jsonb), ('welcome_owner', 'Welcome to PG Finder - Owner Dashboard', '<h1>Welcome {{name}}!</h1><p>Start listing your PG properties and reach thousands of potential tenants.</p>', '["name"]'::jsonb), ('email_verification', 'Verify your email address', '<h1>Verify your email</h1><p>Click the link below to verify your email address:</p><a href="{{verification_link}}">Verify Email</a>', '["verification_link"]'::jsonb), ('password_reset', 'Reset your password', '<h1>Reset Password</h1><p>Click the link below to reset your password:</p><a href="{{reset_link}}">Reset Password</a><p>This link expires in 1 hour.</p>', '["reset_link"]'::jsonb), ('visit_confirmation', 'Visit Confirmed - {{pg_name}}', '<h1>Your visit is confirmed!</h1><p>Property: {{pg_name}}</p><p>Date: {{visit_date}}</p><p>Time: {{visit_time}}</p><p>Owner will contact you at {{user_mobile}}</p>', '["pg_name", "visit_date", "visit_time", "user_mobile"]'::jsonb), ('pg_approved', 'Your PG listing is approved!', '<h1>Congratulations!</h1><p>Your PG "{{pg_name}}" has been approved and is now live on our platform.</p>', '["pg_name"]'::jsonb), ('pg_rejected', 'PG Listing requires changes', '<h1>Action Required</h1><p>Your PG "{{pg_name}}" needs some modifications:</p><p>{{rejection_reason}}</p>', '["pg_name", "rejection_reason"]'::jsonb);

-- Insert system settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, description)
	VALUES ('site_name', 'PG Finder', 'string', 'Website name'), ('site_email', 'support@pgfinder.com', 'string', 'Support email'), ('site_phone', '+91-1234567890', 'string', 'Support phone'), ('max_favorites_per_user', '50', 'number', 'Maximum favorites per user'), ('max_pending_visits_per_user', '5', 'number', 'Maximum pending visits per user'), ('visit_reminder_hours', '24', 'number', 'Hours before visit to send reminder'), ('callback_expiry_days', '7', 'number', 'Days before callback request expires'), ('min_pg_images', '5', 'number', 'Minimum images required for PG'), ('max_pg_images', '20', 'number', 'Maximum images allowed for PG'), ('image_max_size_mb', '10', 'number', 'Maximum image size in MB'), ('search_results_per_page', '20', 'number', 'Search results per page'), ('featured_pg_duration_days', '30', 'number', 'Featured PG display duration'), ('password_min_length', '8', 'number', 'Minimum password length'), ('max_login_attempts', '5', 'number', 'Maximum login attempts before lockout'), ('lockout_duration_minutes', '15', 'number', 'Account lockout duration'), ('session_duration_days', '7', 'number', 'Session duration for remember me'), ('otp_expiry_minutes', '10', 'number', 'OTP expiry time'), ('otp_max_attempts', '3', 'number', 'Maximum OTP attempts'), ('trust_score_min', '0', 'number', 'Minimum trust score'), ('trust_score_max', '100', 'number', 'Maximum trust score'), ('trust_score_default', '50', 'number', 'Default trust score for new owners');

-- =============================================
-- VIEWS FOR COMMON QUERIES
-- =============================================
-- View for PG with full details
CREATE OR REPLACE VIEW pg_details_view AS
SELECT
	p.id,
	p.name,
	p.slug,
	p.description,
	p.summary,
	p.property_type,
	p.city,
	p.state,
	p.address,
	p.latitude,
	p.longitude,
	p.gender_type,
	p.occupancy_type,
	p.furnishing_type,
	p.security_deposit,
	p.food_available,
	p.food_type,
	p.status,
	p.is_featured,
	p.view_count,
	p.favorite_count,
	p.contact_click_count,
	p.created_at,
	p.updated_at,
	o.id AS owner_id,
	o.name AS owner_name,
	o.mobile AS owner_mobile,
	o.is_verified AS owner_verified,
	o.trust_score AS owner_trust_score,
	(
		SELECT
			json_agg(json_build_object('id', pr.id, 'room_type', pr.room_type, 'price_per_month', pr.price_per_month, 'available_beds', pr.available_beds))
		FROM
			pg_rooms pr
		WHERE
			pr.pg_id = p.id) AS rooms,
	(
		SELECT
			json_agg(json_build_object('image_url', pi.image_url, 'is_primary', pi.is_primary))
		FROM
			pg_images pi
		WHERE
			pi.pg_id = p.id
		ORDER BY
			pi.display_order) AS images,
	(
		SELECT
			json_agg(a.name)
		FROM
			pg_amenities pa
			JOIN amenities a ON pa.amenity_id = a.id
		WHERE
			pa.pg_id = p.id) AS amenities,
	(
		SELECT
			avg(rating)
		FROM
			reviews
		WHERE
			pg_id = p.id
			AND is_approved = TRUE) AS avg_rating,
	(
		SELECT
			count(*)
		FROM
			reviews
		WHERE
			pg_id = p.id
			AND is_approved = TRUE) AS review_count
FROM
	pgs p
	JOIN owners o ON p.owner_id = o.id
WHERE
	p.is_deleted = FALSE;

-- View for owner analytics
CREATE OR REPLACE VIEW owner_analytics_view AS
SELECT
	o.id AS owner_id,
	o.name AS owner_name,
	count(DISTINCT p.id) AS total_listings,
	count(DISTINCT CASE WHEN p.status = 'approved' THEN
			p.id
		END) AS active_listings,
	coalesce(sum(p.view_count), 0) AS total_views,
	coalesce(sum(p.favorite_count), 0) AS total_favorites,
	coalesce(sum(p.contact_click_count), 0) AS total_contact_clicks,
	count(DISTINCT v.id) AS total_visits,
	count(DISTINCT CASE WHEN v.status = 'completed' THEN
			v.id
		END) AS completed_visits,
	count(DISTINCT cb.id) AS total_callbacks,
	count(DISTINCT CASE WHEN cb.status = 'converted' THEN
			cb.id
		END) AS converted_callbacks,
	o.trust_score,
	s.status AS subscription_status,
	s.end_date AS subscription_end_date
FROM
	owners o
	LEFT JOIN pgs p ON o.id = p.owner_id
		AND p.is_deleted = FALSE
	LEFT JOIN visits v ON p.id = v.pg_id
	LEFT JOIN callbacks cb ON p.id = cb.pg_id
	LEFT JOIN LATERAL (
		SELECT
			status,
			end_date
		FROM
			subscriptions
		WHERE
			owner_id = o.id
		ORDER BY
			end_date DESC
		LIMIT 1) s ON TRUE
GROUP BY
	o.id,
	o.name,
	o.trust_score,
	s.status,
	s.end_date;

-- View for admin dashboard metrics
CREATE OR REPLACE VIEW admin_dashboard_metrics AS
SELECT
	(
		SELECT
			count(*)
		FROM
			users
		WHERE
			is_blocked = FALSE) AS total_users,
	(
		SELECT
			count(*)
		FROM
			users
		WHERE
			date(created_at) = CURRENT_DATE) AS users_today,
	(
		SELECT
			count(*)
		FROM
			owners
		WHERE
			is_blocked = FALSE) AS total_owners,
	(
		SELECT
			count(*)
		FROM
			owners
		WHERE
			is_verified = TRUE) AS verified_owners,
	(
		SELECT
			count(*)
		FROM
			pgs
		WHERE
			is_deleted = FALSE) AS total_pgs,
	(
		SELECT
			count(*)
		FROM
			pgs
		WHERE
			status = 'approved') AS active_pgs,
	(
		SELECT
			count(*)
		FROM
			pgs
		WHERE
			approval_status = 'pending') AS pending_approvals,
	(
		SELECT
			count(*)
		FROM
			visits
		WHERE
			status = 'pending') AS pending_visits,
	(
		SELECT
			coalesce(sum(amount), 0)
		FROM
			payments
		WHERE
			status = 'success'
			AND date_trunc('month', paid_at) = date_trunc('month', CURRENT_DATE)) AS revenue_current_month,
	(
		SELECT
			coalesce(sum(amount), 0)
		FROM
			payments
		WHERE
			status = 'success'
			AND date_trunc('month', paid_at) = date_trunc('month', CURRENT_DATE - INTERVAL '1 month')) AS revenue_last_month,
	(
		SELECT
			count(*)
		FROM
			subscriptions
		WHERE
			status = 'active') AS active_subscriptions;

-- =============================================
-- FUNCTIONS FOR BUSINESS LOGIC
-- =============================================
-- Function to update PG view count
CREATE OR REPLACE FUNCTION increment_pg_view_count (pg_uuid uuid)
	RETURNS void
	AS $
BEGIN
	UPDATE
		pgs
	SET
		view_count = view_count + 1
	WHERE
		id = pg_uuid;
END;
$
LANGUAGE plpgsql;

-- Function to update PG favorite count
CREATE OR REPLACE FUNCTION update_pg_favorite_count (pg_uuid uuid)
	RETURNS void
	AS $
BEGIN
	UPDATE
		pgs
	SET
		favorite_count = (
			SELECT
				count(*)
			FROM
				favorites
			WHERE
				pg_id = pg_uuid)
	WHERE
		id = pg_uuid;
END;
$
LANGUAGE plpgsql;

-- Trigger to update favorite count when favorite added/removed
CREATE OR REPLACE FUNCTION on_favorite_change ()
	RETURNS TRIGGER
	AS $
BEGIN
	IF TG_OP = 'INSERT' THEN
		PERFORM
			update_pg_favorite_count (NEW.pg_id);
	ELSIF TG_OP = 'DELETE' THEN
		PERFORM
			update_pg_favorite_count (OLD.pg_id);
	END IF;
	RETURN NULL;
END;
$
LANGUAGE plpgsql;

CREATE TRIGGER favorite_count_trigger
	AFTER INSERT OR DELETE ON favorites
	FOR EACH ROW
	EXECUTE FUNCTION on_favorite_change ();

-- Function to calculate owner trust score
CREATE OR REPLACE FUNCTION calculate_owner_trust_score (owner_uuid uuid)
	RETURNS integer
	AS $
DECLARE
	score integer := 50;
	-- Base score
	avg_response_time integer;
	conversion_rate DECIMAL;
	complaint_count integer;
	profile_completeness DECIMAL;
BEGIN
	-- Get owner stats
	SELECT
		o.response_time_avg,
		o.visit_conversion_rate,
		o.complaint_count INTO avg_response_time,
		conversion_rate,
		complaint_count
	FROM
		owners o
	WHERE
		o.id = owner_uuid;
	-- Response time bonus (max +15)
	IF avg_response_time IS NOT NULL THEN
		IF avg_response_time <= 30 THEN
			score := score + 15;
		ELSIF avg_response_time <= 60 THEN
			score := score + 10;
		ELSIF avg_response_time <= 120 THEN
			score := score + 5;
		END IF;
	END IF;
	-- Conversion rate bonus (max +20)
	IF conversion_rate IS NOT NULL THEN
		score := score + (conversion_rate * 0.2)::integer;
	END IF;
	-- Verification bonus (+10)
	IF (
		SELECT
			is_verified
		FROM
			owners
		WHERE
			id = owner_uuid) THEN
		score := score + 10;
	END IF;
	-- Complaint penalty (max -30)
	IF complaint_count > 0 THEN
		score := score - (complaint_count * 5);
	END IF;
	-- Profile completeness bonus (max +10)
	SELECT
		((
				CASE WHEN name IS NOT NULL THEN
					2
				ELSE
					0
				END) + (
				CASE WHEN mobile IS NOT NULL THEN
					2
				ELSE
					0
				END) + (
				CASE WHEN profile_picture IS NOT NULL THEN
					2
				ELSE
					0
				END) + (
				CASE WHEN id_proof_url IS NOT NULL THEN
					2
				ELSE
					0
				END) + (
				CASE WHEN bank_account_number IS NOT NULL THEN
					2
				ELSE
					0
				END)) INTO profile_completeness
	FROM
		owners
	WHERE
		id = owner_uuid;
	score := score + profile_completeness::integer;
	-- Ensure score is within bounds [0, 100]
	IF score < 0 THEN
		score := 0;
	ELSIF score > 100 THEN
		score := 100;
	END IF;
	RETURN score;
END;
$
LANGUAGE plpgsql;

-- Function to get nearby PGs using geospatial query
CREATE OR REPLACE FUNCTION get_nearby_pgs (lat DECIMAL, lng DECIMAL, radius_km integer DEFAULT 5)
	RETURNS TABLE (
		pg_id uuid,
		pg_name varchar,
		distance_km DECIMAL
	)
	AS $
BEGIN
	RETURN QUERY
	SELECT
		p.id,
		p.name,
		round(ST_Distance (p.location::geography, ST_SetSRID (ST_MakePoint (lng, lat), 4326)::geography) / 1000, 2) AS distance_km
	FROM
		pgs p
	WHERE
		p.status = 'approved'
		AND p.is_deleted = FALSE
		AND ST_DWithin (p.location::geography, ST_SetSRID (ST_MakePoint (lng, lat), 4326)::geography, radius_km * 1000)
	ORDER BY
		distance_km;
END;
$
LANGUAGE plpgsql;

-- Function for full-text search with ranking
CREATE OR REPLACE FUNCTION search_pgs (search_query text)
	RETURNS TABLE (
		pg_id uuid,
		pg_name varchar,
		rank real
	)
	AS $
BEGIN
	RETURN QUERY
	SELECT
		p.id,
		p.name,
		ts_rank(to_tsvector('english', coalesce(p.name, '') || ' ' || coalesce(p.description, '') || ' ' || coalesce(p.city, '') || ' ' || coalesce(p.address, '')), plainto_tsquery('english', search_query)) AS rank
	FROM
		pgs p
	WHERE
		p.status = 'approved'
		AND p.is_deleted = FALSE
		AND to_tsvector('english', coalesce(p.name, '') || ' ' || coalesce(p.description, '') || ' ' || coalesce(p.city, '') || ' ' || coalesce(p.address, '')) @@ plainto_tsquery('english', search_query)
	ORDER BY
		rank DESC;
END;
$
LANGUAGE plpgsql;

-- =============================================
-- MATERIALIZED VIEWS FOR ANALYTICS
-- =============================================
-- Daily analytics summary
CREATE MATERIALIZED VIEW daily_analytics AS
SELECT
	date(created_at) AS date,
	'user_registration' AS metric,
	count(*) AS count
FROM
	users
GROUP BY
	date(created_at)
UNION ALL
SELECT
	date(created_at) AS date,
	'owner_registration' AS metric,
	count(*) AS count
FROM
	owners
GROUP BY
	date(created_at)
UNION ALL
SELECT
	date(viewed_at) AS date,
	'pg_views' AS metric,
	count(*) AS count
FROM
	pg_views
GROUP BY
	date(viewed_at)
UNION ALL
SELECT
	date(created_at) AS date,
	'visit_requests' AS metric,
	count(*) AS count
FROM
	visits
GROUP BY
	date(created_at)
UNION ALL
SELECT
	date(paid_at) AS date,
	'payments' AS metric,
	count(*) AS count
FROM
	payments
WHERE
	status = 'success'
GROUP BY
	date(paid_at);

CREATE UNIQUE INDEX idx_daily_analytics ON daily_analytics (date, metric);

-- Refresh command for materialized view (run daily via cron)
-- REFRESH MATERIALIZED VIEW CONCURRENTLY daily_analytics;
-- =============================================
-- COMMENTS FOR DOCUMENTATION
-- =============================================
COMMENT ON TABLE users IS 'End users searching for PG accommodations';

COMMENT ON TABLE owners IS 'PG property owners listing their properties';

COMMENT ON TABLE admins IS 'Platform administrators';

COMMENT ON TABLE pgs IS 'PG property listings';

COMMENT ON TABLE pg_rooms IS 'Different room types and pricing for each PG';

COMMENT ON TABLE pg_images IS 'Images for PG properties';

COMMENT ON TABLE amenities IS 'Master list of available amenities';

COMMENT ON TABLE favorites IS 'User favorites/wishlist';

COMMENT ON TABLE visits IS 'Scheduled property visits';

COMMENT ON TABLE callbacks IS 'User callback requests to owners';

COMMENT ON TABLE reviews IS 'User reviews and ratings for PGs';

COMMENT ON TABLE subscriptions IS 'Owner subscription records';

COMMENT ON TABLE payments IS 'Payment transactions';

COMMENT ON TABLE notifications IS 'All platform notifications';

COMMENT ON TABLE blog_posts IS 'Blog content for SEO and user engagement';

COMMENT ON TABLE search_queries IS 'User search history for analytics';

COMMENT ON TABLE reports IS 'User-reported content for moderation';

-- =============================================
-- END OF SCHEMA
-- =============================================
