-- Insert dummy categories
INSERT INTO categories (id, name, created_at) VALUES 
(1, 'Medicine', CURRENT_TIMESTAMP),
(2, 'Instrument', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert dummy medicines with force update
INSERT INTO medicines (id, name, brand, category_id, batch_number, purchase_price, selling_price, selling_price_per_tablet, elements_per_package, quantity_in_stock, loose_elements_in_stock, expiry_date, barcode, created_at) VALUES 
(1, 'Paracetamol 500mg', 'PharmaCo', 2, '001', 50.00, 75.00, 2.00, 30, 100, 15, '2024-12-31', '1234567890123', CURRENT_TIMESTAMP),
(2, 'Amoxicillin 250mg', 'MediCorp', 2, '002', 120.00, 180.00, 6.00, 20, 50, 8, '2024-10-15', '2345678901234', CURRENT_TIMESTAMP),
(3, 'Ibuprofen 400mg', 'HealthPlus', 2, '003', 80.00, 120.00, NULL, NULL, 75, 0, '2025-02-28', '3456789012345', CURRENT_TIMESTAMP),
(4, 'C Vitamin 1000mg', 'VitaHealth', 2, '004', 30.00, 45.00, 1.50, 20, 200, 25, '2024-08-20', '4567890123456', CURRENT_TIMESTAMP),
(5, 'Syringe 5ml', 'MediSupplies', 1, '005', 15.00, 25.00, NULL, NULL, 500, 0, NULL, '5678901234567', CURRENT_TIMESTAMP),
(6, 'Aspirin 100mg', 'PainRelief Inc', 2, '006', 40.00, 60.00, 2.00, 25, 150, 12, '2025-01-15', '6789012345678', CURRENT_TIMESTAMP),
(7, 'Sample Container 10ml', 'LabTech', 1, '007', 8.00, 15.00, NULL, NULL, 1000, 0, NULL, '7890123456789', CURRENT_TIMESTAMP),
(8, 'Insulin 10ml', 'DiabetiCare', 2, '008', 450.00, 650.00, 65.00, 10, 25, 3, '2024-07-25', '8901234567890', CURRENT_TIMESTAMP),
(9, 'Allergy Tablets 10mg', 'AllerFree', 2, '009', 95.00, 140.00, 4.50, 30, 80, 18, '2025-03-20', '9012345678901', CURRENT_TIMESTAMP),
(10, 'Blood Pressure Monitor', 'HealthDevices', 1, '010', 800.00, 1200.00, NULL, NULL, 15, 0, NULL, '0123456789012', CURRENT_TIMESTAMP),
(11, 'Calcium + D3 600mg', 'BoneStrong', 2, '011', 70.00, 105.00, 3.50, 30, 120, 22, '2025-04-10', '1122334455667', CURRENT_TIMESTAMP),
(12, 'Gloves - Latex', 'ProtectiveGear', 1, '012', 25.00, 40.00, NULL, NULL, 200, 0, NULL, '2233445566778', CURRENT_TIMESTAMP),
(13, 'Antiseptic Cream 50g', 'WoundCare', 1, '013', 45.00, 70.00, NULL, NULL, 65, 0, '2025-05-01', '3344556677889', CURRENT_TIMESTAMP),
(14, 'Vitamin B Complex', 'EnergyPlus', 2, '014', 65.00, 95.00, 3.00, 25, 95, 14, '2024-11-25', '4455667788990', CURRENT_TIMESTAMP),
(15, 'Thermometer Digital', 'MediDevices', 1, '015', 150.00, 220.00, NULL, NULL, 30, 0, NULL, '5566778899001', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET 
  batch_number = EXCLUDED.batch_number,
  name = EXCLUDED.name,
  brand = EXCLUDED.brand,
  category_id = EXCLUDED.category_id,
  purchase_price = EXCLUDED.purchase_price,
  selling_price = EXCLUDED.selling_price,
  selling_price_per_tablet = EXCLUDED.selling_price_per_tablet,
  elements_per_package = EXCLUDED.elements_per_package,
  quantity_in_stock = EXCLUDED.quantity_in_stock,
  loose_elements_in_stock = EXCLUDED.loose_elements_in_stock,
  expiry_date = EXCLUDED.expiry_date,
  barcode = EXCLUDED.barcode;
