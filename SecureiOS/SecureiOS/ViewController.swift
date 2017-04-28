//
//  ViewController.swift
//  SecureiOS
//
//  Created by Fredrik Lillejordet on 28/04/2017.
//  Copyright © 2017 ftl. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet weak var resultLabel: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        demonstration()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func demonstration() {
        let message = "abc1234"
        
        let storage = Storage()
        storage.set(key: "result", value: message)
        let decrypted = storage.get(key: "result")
        
        resultLabel.text = message == decrypted ? "Success! \(decrypted)" : "Failed! \(decrypted)"
    }
}

