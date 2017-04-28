//
//  Storage.swift
//  SecureStorage
//
//  Created by Fredrik Lillejordet on 28/04/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

import LUKeychainAccess

@objc(Storage)
class Storage: NSObject{
    
    func set(key: String, value: String) {
        let keychainAccess = LUKeychainAccess()
        keychainAccess.setObject(value, forKey: key)
    }
    
    func get(key: String) -> String {
        let keychainAccess = LUKeychainAccess()
        return keychainAccess.object(forKey: key) as! String
    }
}
